package com.palja.order_service.application.service;

import com.palja.order_service.application.dto.CouponRes;

import java.util.UUID;

public interface CouponService {

    // 쿠폰 정보 조회
    CouponRes getCoupon(UUID couponId);

    // 쿠폰 사용 처리
    void useCoupon(UUID couponId, UUID orderId);

    // 쿠폰 사용 취소 (주문 취소 시)
    void cancelCoupon(UUID couponId, UUID orderId);
}