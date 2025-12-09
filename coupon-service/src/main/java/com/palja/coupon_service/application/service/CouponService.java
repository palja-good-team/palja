package com.palja.coupon_service.application.service;

import com.palja.coupon_service.application.command.ChangeCouponStatusCommand;
import com.palja.coupon_service.application.command.IssueCouponCommand;
import com.palja.coupon_service.application.command.UseCouponCommand;
import com.palja.coupon_service.application.dto.couponUser.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CouponService {
    CreateCouponUserRes issueCoupon(IssueCouponCommand command);

    UsedCouponUserRes useCoupon(UseCouponCommand command);

    CancelCouponUserRes cancelCoupon(UUID couponUserId, String userId);

    ChangeStatusCouponUserRes changeCouponStatus(ChangeCouponStatusCommand command);

    DeleteCouponUserRes deleteCoupon(UUID couponUserId, String loginId);

    Page<ReadCouponUserRes> getCouponList(String userId, Pageable pageable);

    ReadCouponUserDetailRes getCouponDetail(UUID couponUserId, String userId);
}
