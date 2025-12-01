package com.palja.coupon_service.application.service.impl;

import com.palja.coupon_service.application.command.CreateCouponCommand;
import com.palja.coupon_service.application.dto.CouponDTO;
import com.palja.coupon_service.application.service.CouponManagerService;
import com.palja.coupon_service.domain.entity.Coupon;
import com.palja.coupon_service.domain.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponManagerServiceImpl implements CouponManagerService {

    private final CouponRepository couponRepository;

    @Override
    @Transactional
    public CouponDTO createCoupon(CreateCouponCommand command) {
        log.info("쿠폰 생성 시작");

        Coupon coupon = Coupon.create(
                command.couponName(),
                command.description(),
                command.discountType(),
                command.discountValue(),
                command.totalQuantity(),
                command.maxDiscountAmount(),
                command.minOrderAmount(),
                command.issueStartAt(),
                command.issueEndAt(),
                command.validityDays()
        );

        Coupon savedCoupon = couponRepository.save(coupon);

        return CouponDTO.from(savedCoupon);
    }
}
