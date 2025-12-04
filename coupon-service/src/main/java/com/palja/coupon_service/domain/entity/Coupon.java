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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;

    public static Coupon create(
            String name,
            String description,
            DiscountPolicy discountPolicy,
            Integer totalQuantity,
            AmountPolicy amountPolicy,
            IssuePeriod issuePeriod
    ) {
        validateRequiredFields(name);
        validateQuantity(totalQuantity);

        return Coupon.builder()
                .name(name)
                .description(description)
                .discountPolicy(discountPolicy)
                .totalQuantity(totalQuantity)
                .amountPolicy(amountPolicy)
                .issuePeriod(issuePeriod)
                .status(CouponStatus.ACTIVE)
                .build();
    }

    public void update(
            String name,
            String description,
            Integer newTotalQuantity,
            Integer maxDiscountAmount,
            Integer minOrderAmount,
            LocalDateTime issueStartAt,
            LocalDateTime issueEndAt
    ) {
        validateModifiable();

        if (name != null) {
            if (hasIssued())
                throw new BusinessException(CouponErrorCode.CANNOT_MODIFY_ISSUED_COUPON);
            this.name = name;
        }

        if (description != null)
            this.description = description;

        if (totalQuantity != null) {
            validateTotalQuantityUpdate(totalQuantity);
            this.totalQuantity = newTotalQuantity;
        }

        if (maxDiscountAmount != null || minOrderAmount != null) {
            if (hasIssued())
                throw new BusinessException(CouponErrorCode.CANNOT_MODIFY_ISSUED_COUPON);
            this.amountPolicy = this.amountPolicy.update(maxDiscountAmount, minOrderAmount);
        }

        if (issueStartAt != null || issueEndAt != null) {
            if (hasIssued())
                throw new BusinessException(CouponErrorCode.CANNOT_MODIFY_ISSUED_COUPON);
            this.issuePeriod = this.issuePeriod.update(issueStartAt, issueEndAt);
        }
    }

    public void changeStatus(CouponStatus newStatus) {
        validateStatusTransition(newStatus);

        if (newStatus == CouponStatus.DELETED)
            softDelete();

        this.status = newStatus;
    }

    // 필수 필드 검증
    private static void validateRequiredFields(String name) {
        if (name == null || name.isBlank())
            throw new BusinessException(CouponErrorCode.INVALID_COUPON_NAME);
    }

    // 수량 정책 검증
    private static void validateQuantity(Integer totalQuantity) {
        if (totalQuantity == null)
            // 무제한 발급
            return;

        if (totalQuantity < 1)
            throw new BusinessException(CouponErrorCode.INVALID_QUANTITY);
    }

    // 발행 여부 검증
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
}
