package com.palja.order_service.infrastructure.external;

import com.palja.common.response.ApiResponse;
import com.palja.order_service.infrastructure.external.dto.request.CancelPaymentDTO;
import com.palja.order_service.infrastructure.external.dto.request.CreatePaymentDTO;
import com.palja.order_service.infrastructure.external.dto.response.PaymentCancelDTO;
import com.palja.order_service.infrastructure.external.dto.response.PaymentCreateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "payment-service", path = "/api/v1/payments")
public interface PaymentClient {

    // 결제 생성 요청
    @PostMapping
    ApiResponse<PaymentCreateDTO> createPayment(@RequestBody CreatePaymentDTO request);

    @PostMapping("/{paymentId}/cancel")
    ApiResponse<PaymentCancelDTO> cancelPayment(
            @PathVariable("paymentId") UUID paymentId,
            @RequestBody CancelPaymentDTO request
    );
}