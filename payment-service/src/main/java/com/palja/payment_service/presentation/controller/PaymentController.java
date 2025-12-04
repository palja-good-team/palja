package com.palja.payment_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.payment_service.application.command.FindPaymentListByConditionCommand;
import com.palja.payment_service.application.dto.response.PaymentDetailRes;
import com.palja.payment_service.application.service.PaymentService;
import com.palja.payment_service.domain.vo.PaymentStatus;
import com.palja.payment_service.presentation.dto.request.CancelPaymentReq;
import com.palja.payment_service.presentation.dto.request.CreatePaymentReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDetailRes>> createPayment(
            @Valid @RequestBody CreatePaymentReq req
    ) {
        PaymentDetailRes detail = paymentService.createPayment(req.toCommand());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(detail, "결제가 생성되었습니다."));
    }

    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<ApiResponse<PaymentDetailRes>> cancelPayment(
            @PathVariable UUID paymentId,
            @RequestBody CancelPaymentReq req
    ){
        PaymentDetailRes detail = paymentService.cancelPayment(req.toCommand(paymentId));

        return ResponseEntity
                .ok(ApiResponse.success(detail,"결제가 취소되었습니다."));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentDetailRes>> getPayment(@PathVariable UUID paymentId) {
        PaymentDetailRes detail = paymentService.getPayment(paymentId);

        return ResponseEntity
                .ok(ApiResponse.success(detail, "결제 단건 조회에 성공했습니다."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PaymentDetailRes>>> getPayments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) UUID orderId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PaymentStatus paymentStatus = (status != null) ? PaymentStatus.valueOf(status) : null;

        FindPaymentListByConditionCommand command = new FindPaymentListByConditionCommand(
                paymentStatus,
                userId,
                orderId,
                startDate,
                endDate
        );

        PageRequest pageRequest = PageRequest.of(page, size);
        var pageResult = paymentService.searchPayments(command, pageRequest);

        PageResponse<PaymentDetailRes> detail = PageResponse.from(pageResult);

        return ResponseEntity
                .ok(ApiResponse.success(detail, "결제 목록 조회에 성공했습니다."));
    }

    @DeleteMapping("/manager/{paymentId}")
    public ResponseEntity<ApiResponse<String>> deletePayment(
            @PathVariable UUID paymentId
    ){
        paymentService.deletePayment(paymentId);
        return new ResponseEntity<>(ApiResponse.success("결제가 삭제되었습니다."),HttpStatus.OK);
    }
}
