package com.palja.order_service.application.port;

import com.palja.order_service.application.dto.external.CouponUserDetailRes;

import java.math.BigDecimal;
import java.util.UUID;

public interface CouponClient {

    // 쿠폰 정보 조회
    CouponUserDetailRes getCoupon(UUID couponUserId);

    // 쿠폰 사용 처리
    void useCoupon(UUID couponUserId, UUID orderId, BigDecimal couponDiscountAmount);

    // 쿠폰 사용 취소 (주문 취소 시)
    void cancelCoupon(UUID couponUserId, UUID orderId);
}