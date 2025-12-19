package com.palja.order_service.infrastructure.external.client;

import com.palja.common.response.ApiResponse;
import com.palja.order_service.infrastructure.external.client.dto.request.UseCouponDTO;
import com.palja.order_service.infrastructure.external.client.dto.response.CancelCouponUserDTO;
import com.palja.order_service.infrastructure.external.client.dto.response.CouponUserDetailDTO;
import com.palja.order_service.infrastructure.external.client.dto.response.UsedCouponUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "coupon-service", path = "/api/v1/coupons")
public interface CouponFeignClient {

    // 사용자 쿠폰 단건 조회
    @GetMapping("/me/{couponUserId}")
    ApiResponse<CouponUserDetailDTO> getMyCouponDetail(
            @PathVariable("couponUserId") UUID couponUserId
    );

    // 쿠폰 사용
    @PostMapping("/{couponUserId}/use")
    ApiResponse<UsedCouponUserDTO> useCoupon(
            @PathVariable("couponUserId") UUID couponUserId,
            @RequestBody UseCouponDTO request
    );

    // 쿠폰 사용 취소
    @PutMapping("/{couponUserId}/cancel")
    ApiResponse<CancelCouponUserDTO> cancelCoupon(
            @PathVariable("couponUserId") UUID couponUserId
    );
}