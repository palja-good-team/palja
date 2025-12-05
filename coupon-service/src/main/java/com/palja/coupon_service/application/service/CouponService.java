package com.palja.coupon_service.application.service;

import com.palja.coupon_service.application.command.IssueCouponCommand;
import com.palja.coupon_service.application.dto.CouponUserRes;

public interface CouponService {
    CouponUserRes issueCoupon(IssueCouponCommand command);
}
