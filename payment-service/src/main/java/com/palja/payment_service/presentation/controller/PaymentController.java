package com.palja.payment_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.payment_service.application.dto.response.PaymentDetailRes;
import com.palja.payment_service.application.service.PaymentService;
import com.palja.payment_service.presentation.dto.request.CancelPaymentReq;
import com.palja.payment_service.presentation.dto.request.CreatePaymentReq;
import com.palja.payment_service.presentation.dto.response.PaymentRes;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    ) {
        PaymentDetailRes detail = paymentService.cancelPayment(req.toCommand(paymentId));
        PaymentRes res = PaymentRes.from(detail);

        return ResponseEntity
                .ok(ApiResponse.success(res, "결제가 취소되었습니다."));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentRes>> getPayment(@PathVariable UUID paymentId) {
        PaymentDetailRes detail = paymentService.getPayment(paymentId);
        PaymentRes res = PaymentRes.from(detail);

        return ResponseEntity
                .ok(ApiResponse.success(res, "결제 단건 조회에 성공했습니다."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PaymentRes>>> getPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<PaymentDetailRes> pageResult = paymentService.getPayments(pageRequest);
        PageResponse<PaymentRes> res = PageResponse.from(pageResult.map(PaymentRes::from));

        return ResponseEntity
                .ok(ApiResponse.success(res, "결제 목록 조회에 성공했습니다."));
    }
}
