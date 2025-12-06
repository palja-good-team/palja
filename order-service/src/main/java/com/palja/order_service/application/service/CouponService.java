package com.palja.order_service.application.service;

import com.palja.order_service.application.dto.CouponRes;

import java.util.UUID;

public interface CouponService {

    CouponRes getCoupon(UUID couponId);

    void useCoupon(UUID couponId, UUID orderId);
}