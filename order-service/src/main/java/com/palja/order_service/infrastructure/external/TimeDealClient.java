package com.palja.order_service.infrastructure.external;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "time-deal", path = "api/v1/time-deals")
public interface TimeDealClient {
}