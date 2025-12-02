package com.palja.order_service.infrastructure.external.adapter;

import com.palja.order_service.application.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponAdapter implements CouponService {

    // TODO: 쿠폰 서비스 연동 시 CouponClient 주입 및 구현 추가
    // private final CouponClient couponClient;
}