package com.palja.payment_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.payment_service.application.command.FindPaymentLogListByConditionCommand;
import com.palja.payment_service.application.dto.response.ReadPaymentLogRes;
import com.palja.payment_service.application.service.PaymentLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentLogController {

    private final PaymentLogService paymentLogService;

    @GetMapping("/payments/{paymentId}/logs")
    public ResponseEntity<ApiResponse<List<ReadPaymentLogRes>>> getPaymentLogsByPaymentId(
            @PathVariable UUID paymentId
    ) {
        List<ReadPaymentLogRes> res = paymentLogService.getLogsByPaymentId(paymentId);
        return ResponseEntity.ok(
                ApiResponse.success(res, "PaymentId에 해당되는 결제 로그 목록 조회에 성공했습니다.")
        );
    }

    @GetMapping("/payment-logs")
    public ResponseEntity<ApiResponse<PageResponse<ReadPaymentLogRes>>> getPaymentLogs(
            @RequestParam(required = false) UUID paymentId,
            @RequestParam(required = false) UUID orderId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        FindPaymentLogListByConditionCommand command = new FindPaymentLogListByConditionCommand(
                paymentId,
                orderId,
                status,
                startDate,
                endDate
        );

        PageRequest pageRequest = PageRequest.of(page, size);
        var pageResult = paymentLogService.searchLogs(command, pageRequest);

        PageResponse<ReadPaymentLogRes> detail = PageResponse.from(pageResult);

        return ResponseEntity
                .ok(ApiResponse.success(detail, "검색 결과에 따른 결제 로그 목록 조회에 성공했습니다."));
    }

    /*
        스케줄러를 이용해서 1년 지난 결제 로그를 삭제하지만,
        API를 만들어 수동으로 삭제할 수 있도록 테스트했습니다.
        실제 운영 환경에서는 스케줄러에 의해 1년 지난 결제 로그가 자동으로 삭제됩니다.
     */
    @PostMapping("/payment-logs/delete")
    public ResponseEntity<ApiResponse<String>> deleteOldLogs(){
        paymentLogService.deleteOldLogs();
        return ResponseEntity
                .ok(ApiResponse.success("1년 지난 결제 로그가 삭제되었습니다."));
    }
}
