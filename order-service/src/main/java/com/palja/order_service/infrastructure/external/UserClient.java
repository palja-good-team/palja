package com.palja.order_service.infrastructure.external;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user", path = "/api/v1")
public interface UserClient {
}