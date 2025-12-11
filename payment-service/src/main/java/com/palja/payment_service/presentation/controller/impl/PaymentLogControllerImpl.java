package com.palja.payment_service.presentation.controller.impl;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.payment_service.application.command.FindPaymentLogListByConditionCommand;
import com.palja.payment_service.application.dto.response.ReadPaymentLogRes;
import com.palja.payment_service.application.service.PaymentLogService;
import com.palja.payment_service.presentation.controller.PaymentLogController;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.palja.common.annotation.RequiredRole;
import com.palja.common.vo.UserRole;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentLogControllerImpl implements PaymentLogController {

    private final PaymentLogService paymentLogService;

    @Override
    @GetMapping("/payments/{paymentId}/logs")
    @RequiredRole({UserRole.MANAGER})
    public ResponseEntity<ApiResponse<List<ReadPaymentLogRes>>> getPaymentLogsByPaymentId(
            @PathVariable UUID paymentId
    ) {
        List<ReadPaymentLogRes> res = paymentLogService.getLogsByPaymentId(paymentId);
        return ResponseEntity.ok(
                ApiResponse.success(res, "PaymentId에 해당되는 결제 로그 목록 조회에 성공했습니다.")
        );
    }

    @Override
    @GetMapping("/payment-logs")
    @RequiredRole({UserRole.MANAGER})
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

    @Override
    @PostMapping("/payment-logs/delete")
    @RequiredRole({UserRole.MANAGER})
    public ResponseEntity<ApiResponse<String>> deleteOldLogs(){
        paymentLogService.deleteOldLogs();
        return ResponseEntity
                .ok(ApiResponse.success("1년 지난 결제 로그가 삭제되었습니다."));
    }
}
