package com.palja.coupon_service.application.service;

import com.palja.coupon_service.application.command.CreateCouponCommand;
import com.palja.coupon_service.application.dto.CouponDTO;

public interface CouponManagerService {

    CouponDTO createCoupon(CreateCouponCommand command);
}
