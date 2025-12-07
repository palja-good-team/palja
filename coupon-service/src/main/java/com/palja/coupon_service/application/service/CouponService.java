package com.palja.coupon_service.application.service;

import com.palja.coupon_service.application.command.IssueCouponCommand;
import com.palja.coupon_service.application.dto.CouponUserRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponService {
    CouponUserRes issueCoupon(IssueCouponCommand command);

    Page<CouponUserRes> getCouponList(String userId, Pageable pageable);
}
