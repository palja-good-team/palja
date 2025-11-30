package com.palja.payment_service.infrastructure.external;

import com.palja.payment_service.infrastructure.config.TossFeignConfig;
import com.palja.payment_service.infrastructure.dto.request.TossPaymentConfirmReq;
import com.palja.payment_service.infrastructure.dto.response.TossPaymentRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "tossPaymentsClient",
        url = "${toss.base-url}",
        configuration = TossFeignConfig.class
)
public interface TossPaymentClient {

    @GetMapping("/v1/payments/{paymentKey}")
    TossPaymentRes getPayment(@PathVariable String paymentKey);

    @PostMapping("/v1/payments/confirm")
    TossPaymentRes confirm(@RequestBody TossPaymentConfirmReq req);
}

