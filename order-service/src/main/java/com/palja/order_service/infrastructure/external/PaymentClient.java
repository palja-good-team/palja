package com.palja.order_service.infrastructure.external;

import com.palja.common.response.ApiResponse;
import com.palja.order_service.infrastructure.external.dto.request.CreatePaymentDTO;
import com.palja.order_service.infrastructure.external.dto.response.PaymentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", path = "/api/v1/payments")
public interface PaymentClient {

    // 결제 생성 요청
    @PostMapping
    ApiResponse<PaymentDTO> createPayment(@RequestBody CreatePaymentDTO request);
}