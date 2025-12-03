package com.palja.coupon_service.application.service;

import com.palja.coupon_service.application.command.CreateCouponCommand;
import com.palja.coupon_service.application.dto.CouponRes;
import com.palja.coupon_service.application.dto.CouponDetailRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CouponManagerService {

    CouponRes createCoupon(CreateCouponCommand command);

    Page<CouponRes> getCouponList(Pageable pageable);

    CouponDetailRes getCouponDetail(UUID couponId);
}
