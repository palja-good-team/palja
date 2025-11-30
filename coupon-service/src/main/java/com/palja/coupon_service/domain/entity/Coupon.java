package com.palja.coupon_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import com.palja.coupon_service.domain.vo.CouponStatus;
import com.palja.coupon_service.domain.vo.DiscountType;
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

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType; // 할인 타입

    @Column(nullable = false)
    private Integer discountValue; // 할인율(%) 또는 할인금액

    @Column(nullable = false)
    private Integer totalQuantity; // 총 발급 가능 수량

    @Column(nullable = false)
    private Integer issuedQuantity = 0; // 현재 발급된 수량

    @Column
    private Integer maxDiscountAmount; // 최대 할인 금액

    @Column
    private Integer minOrderAmount; // 최소 주문 금액

    @Column(nullable = false)
    private LocalDateTime issueStartAt; // 발급 시작일

    @Column(nullable = false)
    private LocalDateTime issueEndAt; // 발급 종료일

    @Column(nullable = false)
    private Integer validityDays; // 유효 기간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;
}
