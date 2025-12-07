package com.palja.coupon_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.coupon_service.application.command.IssueCouponCommand;
import com.palja.coupon_service.application.dto.CouponUserDetailRes;
import com.palja.coupon_service.application.dto.CouponUserRes;
import com.palja.coupon_service.application.service.CouponService;
import com.palja.coupon_service.domain.entity.Coupon;
import com.palja.coupon_service.domain.entity.CouponUser;
import com.palja.coupon_service.domain.repository.CouponRepository;
import com.palja.coupon_service.domain.repository.CouponUserRepository;
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
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponUserRepository couponUserRepository;

    @Override
    @Transactional
    public CouponUserRes issueCoupon(IssueCouponCommand command) {
        log.info("쿠폰 발급 시작 userId={} couponId={}", command.userId(), command.couponId());

        Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(command.couponId())
                .orElseThrow(() -> new BusinessException(CouponErrorCode.COUPON_NOT_FOUND));

        validateCouponIssue(coupon, command.userId());

        coupon.increaseIssuedQuantity();

        CouponUser couponUser = CouponUser.issue(coupon, command.userId());

        CouponUser issuedCoupon = couponUserRepository.save(couponUser);

        log.info("쿠폰 발급 성공 issuedCouponID={}", issuedCoupon.getCoupon().getId());
        return CouponUserRes.from(couponUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CouponUserRes> getCouponList(String userId, Pageable pageable) {
        log.info("쿠폰 목록 조회 시작 userId={}", userId);
        return couponUserRepository.findAllByUserIdAndDeletedAtIsNull(userId, pageable)
                .map(CouponUserRes::from);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponUserDetailRes getCouponDetail(UUID couponUserId, String userId) {
        log.info("쿠폰 상세 조회 시작 couponId={} userId={}", couponUserId, userId);

        CouponUser couponUser = couponUserRepository.findByIdAndUserIdAndDeletedAtIsNull(couponUserId, userId)
                .orElseThrow(() -> new BusinessException(CouponErrorCode.USER_COUPON_NOT_FOUND));

        return CouponUserDetailRes.from(couponUser);
    }

    // 쿠폰 발급 검증
    private void validateCouponIssue(Coupon coupon, String userId) {

        coupon.validateIssuable();

        coupon.validateIssuePeriod();

        if (couponUserRepository.existsByCoupon_IdAndUserIdAndDeletedAtIsNull(coupon.getId(), userId))
            throw new BusinessException(CouponErrorCode.DUPLICATE_COUPON_ISSUE);
    }
}
