package com.palja.payment_service.infrastructure.external;

import com.palja.common.response.ApiResponse;
import com.palja.payment_service.infrastructure.external.dto.response.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "order-service", path = "/api/v1/orders")
public interface OrderFeignClient {
    @GetMapping("/{orderId}")
    ApiResponse<OrderDTO> getOrderByOrderId(@PathVariable("orderId") UUID orderId);
}
