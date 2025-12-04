package com.palja.order_service.infrastructure.external;

import com.palja.common.response.ApiResponse;
import com.palja.order_service.infrastructure.external.dto.request.UseCouponDTO;
import com.palja.order_service.infrastructure.external.dto.response.CouponDTO;
import com.palja.order_service.infrastructure.external.dto.response.CouponUseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "coupon", path = "/api/v1/coupons")
public interface CouponClient {

    // 쿠폰 단건 조회
    @GetMapping("/{couponId}")
    ApiResponse<CouponDTO> getCoupon(@PathVariable("couponId") UUID couponId);

    // 쿠폰 사용
    @PostMapping("/{couponId}/use")
    ApiResponse<CouponUseDTO> useCoupon(@PathVariable UUID couponId, @RequestBody UseCouponDTO request);
}