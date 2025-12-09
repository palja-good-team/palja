package com.palja.coupon_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.coupon_service.application.command.ChangeCouponStatusCommand;
import com.palja.coupon_service.application.command.CreateCouponCommand;
import com.palja.coupon_service.application.command.UpdateCouponCommand;
import com.palja.coupon_service.application.dto.coupon.*;
import com.palja.coupon_service.application.service.CouponManagerService;
import com.palja.coupon_service.domain.entity.Coupon;
import com.palja.coupon_service.domain.repository.CouponRepository;
import com.palja.coupon_service.domain.vo.*;
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
    public CreateCouponRes createCoupon(CreateCouponCommand command) {
        log.info("쿠폰 생성 시작");

        validateCouponNameDuplicate(command.couponName());

        Coupon coupon = Coupon.create(
                command.couponName(),
                command.description(),
                DiscountPolicy.of(DiscountType.valueOf(command.discountType().toUpperCase()), command.discountValue()),
                command.totalQuantity(),
                AmountPolicy.of(command.maxDiscountAmount(), command.minOrderAmount()),
                IssuePeriod.of(command.issueStartAt(), command.issueEndAt()),
                command.usageDays()
        );

        Coupon savedCoupon = couponRepository.save(coupon);

        log.info("쿠폰 생성 완료 - couponId={}", savedCoupon.getId());
        return CreateCouponRes.from(savedCoupon);
    }

    @Override
    @Transactional
    public UpdateCouponRes updateCoupon(UpdateCouponCommand command) {
        log.info("쿠폰 수정 시작 - couponId={}", command.couponId());

        Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(command.couponId())
                .orElseThrow(() -> new BusinessException(CouponErrorCode.COUPON_NOT_FOUND));

        if (command.couponName() != null && !coupon.getName().equals(command.couponName()))
            validateCouponNameDuplicate(command.couponName());

        coupon.update(
                command.couponName(),
                command.description(),
                command.totalQuantity(),
                command.maxDiscountAmount(),
                command.minOrderAmount(),
                command.issueStartAt(),
                command.issueEndAt(),
                command.usageDays()
        );

        log.info("쿠폰 수정 완료 - couponId={}", command.couponId());
        return UpdateCouponRes.from(coupon);
    }

    @Override
    @Transactional
    public ChangeStatusCouponRes changeCouponStatus(ChangeCouponStatusCommand command) {
        log.info("쿠폰 상태 변경 시작 - couponId={} status={}", command.couponId(), command.status());

        Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(command.couponId())
                .orElseThrow(() -> new BusinessException(CouponErrorCode.COUPON_NOT_FOUND));

        CouponStatus oldStatus = coupon.getStatus();

        coupon.changeStatus(CouponStatus.valueOf(command.status().toUpperCase()));

        log.info("쿠폰 상태 변경 완료 - couponId={} status={} -> {}", command.couponId(), oldStatus, command.status());
        return ChangeStatusCouponRes.from(coupon);
    }

    @Override
    public Page<ReadCouponRes> getCouponList(Pageable pageable) {
        log.info("쿠폰 목록 조회 시작");
        return couponRepository.findAllByDeletedAtIsNull(pageable)
                .map(ReadCouponRes::from);
    }

    @Override
    public ReadCouponDetailRes getCouponDetail(UUID couponId) {
        log.info("쿠폰 조회 시작 - couponId={}", couponId);

        Coupon coupon = couponRepository.findByIdAndDeletedAtIsNull(couponId)
                .orElseThrow(() -> new BusinessException(CouponErrorCode.COUPON_NOT_FOUND));

        return ReadCouponDetailRes.from(coupon);
    }

    // 쿠폰명 중복 체크
    private void validateCouponNameDuplicate(String couponName) {
        if (couponRepository.existsByNameAndDeletedAtIsNull(couponName)) {
            log.warn("쿠폰명 중복 - couponName: {}", couponName);
            throw new BusinessException(CouponErrorCode.DUPLICATE_COUPON_NAME);
        }
    }
}
