package com.palja.coupon_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.coupon_service.application.command.CreateCouponCommand;
import com.palja.coupon_service.application.dto.CouponRes;
import com.palja.coupon_service.application.dto.CouponDetailRes;
import com.palja.coupon_service.application.service.CouponManagerService;
import com.palja.coupon_service.domain.entity.Coupon;
import com.palja.coupon_service.domain.repository.CouponRepository;
import com.palja.coupon_service.domain.vo.AmountPolicy;
import com.palja.coupon_service.domain.vo.DiscountPolicy;
import com.palja.coupon_service.domain.vo.IssuePeriod;
import com.palja.coupon_service.exception.CouponErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponManagerServiceImpl implements CouponManagerService {

    private final CouponRepository couponRepository;

    @Override
    @Transactional
    public CouponRes createCoupon(CreateCouponCommand command) {
        log.info("쿠폰 생성 시작");

        Coupon coupon = Coupon.create(
                command.couponName(),
                command.description(),
                DiscountPolicy.of(command.discountType(),  command.discountValue()),
                command.totalQuantity(),
                AmountPolicy.of(command.maxDiscountAmount(), command.minOrderAmount()),
                IssuePeriod.of(command.issueStartAt(), command.issueEndAt())
        );

        Coupon savedCoupon = couponRepository.save(coupon);

        return CouponRes.from(savedCoupon);
    }

    @Override
    public Page<CouponRes> getCouponList(Pageable pageable) {
        log.info("쿠폰 목록 조회 시작");
        return couponRepository.findAllByDeletedAtIsNull(pageable)
                .map(CouponRes::from);
    }

    @Override
    public CouponDetailRes getCouponDetail(UUID couponId) {
        log.info("쿠폰 조회 시작 - couponId={}", couponId);

        Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(couponId)
                .orElseThrow(() -> new BusinessException(CouponErrorCode.COUPON_NOT_FOUND));

        return CouponDetailRes.from(coupon);
    }
}
