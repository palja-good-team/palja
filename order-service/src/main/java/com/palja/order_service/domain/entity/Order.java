package com.palja.order_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import com.palja.order_service.domain.vo.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", nullable = false, updatable = false)
    private UUID orderId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "coupon_id")
    private UUID couponId;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "product_total_amount", nullable = false)
    private Long productTotalAmount;

    @Column(name = "item_discount_amount", nullable = false)
    private Long itemDiscountAmount;

    @Column(name = "coupon_discount_amount")
    private Long couponDiscountAmount;

    @Column(name = "delivery_fee", nullable = false)
    private Long deliveryFee;

    @Column(name = "final_amount", nullable = false)
    private Long finalAmount;

    @Column(name = "coupon_name", length = 100)
    private String couponName;

    @Column(name = "time_deal_order", nullable = false)
    private Boolean timeDealOrder;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "canceled_by")
    private Long canceledBy;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;
}