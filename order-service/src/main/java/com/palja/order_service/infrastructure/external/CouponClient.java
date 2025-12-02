package com.palja.order_service.infrastructure.external;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "coupon", path = "/api/v1/coupons")
public interface CouponClient {
}