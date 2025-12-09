package com.palja.coupon_service.application.service;

import com.palja.coupon_service.application.command.ChangeCouponStatusCommand;
import com.palja.coupon_service.application.command.CreateCouponCommand;
import com.palja.coupon_service.application.command.UpdateCouponCommand;
import com.palja.coupon_service.application.dto.coupon.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CouponManagerService {

    CreateCouponRes createCoupon(CreateCouponCommand command);

    UpdateCouponRes updateCoupon(UpdateCouponCommand command);

    ChangeStatusCouponRes changeCouponStatus(ChangeCouponStatusCommand command);

    Page<ReadCouponRes> getCouponList(Pageable pageable);

    ReadCouponDetailRes getCouponDetail(UUID couponId);

}
