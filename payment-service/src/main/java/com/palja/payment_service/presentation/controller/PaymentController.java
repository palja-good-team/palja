package com.palja.payment_service.presentation.controller;

import com.palja.common.response.ApiResponse;
import com.palja.payment_service.application.dto.response.PaymentDetailRes;
import com.palja.payment_service.application.service.PaymentService;
import com.palja.payment_service.presentation.dto.request.CreatePaymentReq;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/payments")
    public ApiResponse<PaymentDetailRes> createPayment(@RequestBody CreatePaymentReq req){
        PaymentDetailRes result = paymentService.createPayment(req.toCommand());
        return ApiResponse.success(result, HttpStatus.CREATED.toString());
    }
}
