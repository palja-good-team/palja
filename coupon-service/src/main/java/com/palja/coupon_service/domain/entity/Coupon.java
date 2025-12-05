package com.palja.coupon_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import com.palja.common.exception.BusinessException;
import com.palja.coupon_service.domain.vo.*;
import com.palja.coupon_service.exception.CouponErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String name;

    private String description;

    @Embedded
    private DiscountPolicy discountPolicy; // 할인율(%) 또는 할인금액

    @Column
    private Integer totalQuantity; // 총 발급 가능 수량

    @Builder.Default
    private Integer issuedQuantity = 0; // 현재 발급된 수량

    @Embedded
    private AmountPolicy amountPolicy;

    @Embedded
    private IssuePeriod issuePeriod; // 발급 시작일, 종료일

    @Column(nullable = false)
    private Integer usageDays; // 사용 기간 (발행일로부터 N일)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;

    public static Coupon create(
            String name,
            String description,
            DiscountPolicy discountPolicy,
            Integer totalQuantity,
            AmountPolicy amountPolicy,
            IssuePeriod issuePeriod,
            Integer usageDays
    ) {
        validateRequiredFields(name, usageDays);
        validateQuantity(totalQuantity);

        return Coupon.builder()
                .name(name)
                .description(description)
                .discountPolicy(discountPolicy)
                .totalQuantity(totalQuantity)
                .amountPolicy(amountPolicy)
                .issuePeriod(issuePeriod)
                .usageDays(usageDays)
                .status(CouponStatus.ACTIVE)
                .build();
    }

    public void update(
            String newName,
            String newDescription,
            Integer newTotalQuantity,
            Integer newMaxDiscountAmount,
            Integer newMinOrderAmount,
            LocalDateTime newIssueStartAt,
            LocalDateTime newIssueEndAt,
            Integer newUsageDays
    ) {
        validateModifiable();

        if (newName != null) {
            if (hasIssued())
                throw new BusinessException(CouponErrorCode.CANNOT_MODIFY_ISSUED_COUPON);
            this.name = newName;
        }

        if (newDescription != null)
            this.description = newDescription;

        if (newTotalQuantity != null) {
            validateTotalQuantityUpdate(totalQuantity);
            this.totalQuantity = newTotalQuantity;
        }

        if (newMaxDiscountAmount != null || newMinOrderAmount != null) {
            if (hasIssued())
                throw new BusinessException(CouponErrorCode.CANNOT_MODIFY_ISSUED_COUPON);
            this.amountPolicy = this.amountPolicy.update(newMaxDiscountAmount, newMinOrderAmount);
        }

        if (newIssueStartAt != null || newIssueEndAt != null) {
            if (hasIssued())
                throw new BusinessException(CouponErrorCode.CANNOT_MODIFY_ISSUED_COUPON);
            this.issuePeriod = this.issuePeriod.update(newIssueStartAt, newIssueEndAt);
        }

        if (newUsageDays != null) {
            if (hasIssued())
                throw new BusinessException(CouponErrorCode.CANNOT_MODIFY_ISSUED_COUPON);

            if (newUsageDays < 1)
                throw new BusinessException(CouponErrorCode.INVALID_VALIDITY_DAYS);

            this.usageDays = newUsageDays;
        }
    }

    public void changeStatus(CouponStatus newStatus) {
        validateStatusTransition(newStatus);

        if (newStatus == CouponStatus.DELETED)
            softDelete();

        this.status = newStatus;
    }

    public void increaseIssuedQuantity() {
        if (totalQuantity != null && issuedQuantity >= totalQuantity)
            throw new BusinessException(CouponErrorCode.COUPON_EXHAUSTED);

        this.issuedQuantity++;
    }

    // 필수 필드 검증
    private static void validateRequiredFields(String name, Integer usageDays) {
        if (name == null || name.isBlank())
            throw new BusinessException(CouponErrorCode.INVALID_COUPON_NAME);

        if (usageDays == null || usageDays < 1)
            throw new BusinessException(CouponErrorCode.INVALID_VALIDITY_DAYS);
    }

    // 수량 정책 검증
    private static void validateQuantity(Integer totalQuantity) {
        if (totalQuantity == null)
            // 무제한 발급
            return;

        if (totalQuantity < 1)
            throw new BusinessException(CouponErrorCode.INVALID_QUANTITY);
    }

    // 발행된 쿠폰인지 검증
    public boolean hasIssued() {
        return this.issuedQuantity != null && this.issuedQuantity > 0;
    }

    // 수정 가능 검증 (삭제, 만료된 쿠폰은 불가)
    public void validateModifiable() {
        if (this.status == CouponStatus.DELETED || this.status == CouponStatus.EXPIRED)
            throw new BusinessException(CouponErrorCode.CANNOT_MODIFY_DELETED_OR_EXPIRED_COUPON);
    }

    // 총 발급 수량 검증
    private void validateTotalQuantityUpdate(Integer newTotalQuantity) {
        if (this.issuedQuantity != null && newTotalQuantity < this.issuedQuantity)
            throw new BusinessException(CouponErrorCode.INVALID_QUANTITY);
    }

    // 쿠폰 상태 변경 검증
    private void validateStatusTransition(CouponStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new BusinessException(CouponErrorCode.INVALID_STATUS_TRANSITION);
        }
    }

    // 쿠폰 발급 가능 상태 검증
    public void validateIssuable() {
        if (this.status != CouponStatus.ACTIVE)
            throw new BusinessException(CouponErrorCode.COUPON_NOT_AVAILABLE);
    }

    // 쿠폰 발행 기간 검증
    public void validateIssuePeriod() {
        LocalDateTime now = LocalDateTime.now();
        issuePeriod.validateIssuePeriod(now);
    }
}
