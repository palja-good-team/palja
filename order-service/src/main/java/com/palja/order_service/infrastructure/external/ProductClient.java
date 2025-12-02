package com.palja.order_service.infrastructure.external;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "product", path = "/api/v1/products")
public interface ProductClient {
}