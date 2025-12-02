package com.palja.payment_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.payment_service.application.dto.response.PaymentDetailRes;
import com.palja.payment_service.application.service.PaymentService;
import com.palja.payment_service.presentation.dto.request.CancelPaymentReq;
import com.palja.payment_service.presentation.dto.request.CreatePaymentReq;
import com.palja.payment_service.presentation.dto.response.PaymentRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentRes>> createPayment(
            @Valid @RequestBody CreatePaymentReq req
    ) {
        PaymentDetailRes detail = paymentService.createPayment(req.toCommand());
        PaymentRes res = PaymentRes.from(detail);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(res, "결제가 생성되었습니다."));
    }

    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<ApiResponse<PaymentRes>> cancelPayment(
            @PathVariable UUID paymentId,
            @RequestBody CancelPaymentReq req
    ){
        PaymentDetailRes result = paymentService.cancelPayment(req.toCommand(paymentId));
        PaymentRes res = PaymentRes.from(result);

        return ResponseEntity
                .ok(ApiResponse.success(res,"결제가 취소되었습니다."));
    }
}
