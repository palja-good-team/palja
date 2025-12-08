package com.palja.coupon_service.application.service;

import com.palja.coupon_service.application.command.IssueCouponCommand;
import com.palja.coupon_service.application.command.UseCouponCommand;
import com.palja.coupon_service.application.dto.CouponUserDetailRes;
import com.palja.coupon_service.application.dto.CouponUserRes;
import com.palja.coupon_service.application.dto.UsedCouponUserRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CouponService {
    CouponUserRes issueCoupon(IssueCouponCommand command);

    UsedCouponUserRes useCoupon(UseCouponCommand command);

    Page<CouponUserRes> getCouponList(String userId, Pageable pageable);

    CouponUserDetailRes getCouponDetail(UUID couponUserId, String userId);
}
