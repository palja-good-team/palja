package com.palja.coupon_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import com.palja.coupon_service.domain.vo.CouponUserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Column(nullable = false)
    private LocalDateTime expireAt; // 쿠폰 만료일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponUserStatus status;

    private LocalDateTime usedAt; // 사용일시

    private UUID orderId; // 사용한 주문 ID
    
    private Long discountAmount; // 할인된 금액

    public static CouponUser issue(Coupon coupon, String userId) {
        LocalDateTime expireAt = LocalDateTime.now().plusDays(coupon.getUsageDays());
        return CouponUser.builder()
                .coupon(coupon)
                .userId(userId)
                .expireAt(expireAt)
                .status(CouponUserStatus.ISSUED)
                .build();
    }
}
