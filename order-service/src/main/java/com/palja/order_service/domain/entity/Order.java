package com.palja.order_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import com.palja.order_service.domain.vo.OrderAmount;
import com.palja.order_service.domain.vo.OrderStatus;
import com.palja.order_service.domain.vo.Recipient;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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
    private Long userId;

    @Column(name = "coupon_id")
    private UUID couponId;

    @Column(name = "payment_id")
    private UUID paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    @Embedded
    private OrderAmount orderAmount;

    @Column(name = "coupon_name", length = 100)
    private String couponName;

    @Column(name = "time_deal_order", nullable = false)
    private Boolean timeDealOrder;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "canceled_by")
    private String canceledBy;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    // 연관관계 (도메인상 Order → 자식 사용, JPA owner는 자식)
    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private OrderItem orderItem;

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private OrderDelivery delivery;

    // 주문 생성
    // 주문, 주문상품, 배송정보를 한 번에 생성
    public static Order create(
            Long userId,
            UUID productId,
            String productName,
            BigDecimal unitPrice,
            int quantity,
            UUID timeDealId,
            BigDecimal timeDealPrice,
            UUID couponId,
            String couponName,
            BigDecimal couponDiscountAmount,
            BigDecimal deliveryFee,
            Recipient recipient
    ) {
        // 1. 주문자 검증
        validateUserId(userId);
        // 2. 주문 상품 검증
        validateProductId(productId);

        // 3. 타임딜 주문 여부 결정
        boolean isTimeDealOrder = (timeDealId != null && timeDealPrice != null);

        // 4. Order 생성 (아직 자식 없음)
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.CREATED)
                .timeDealOrder(isTimeDealOrder)
                .couponId(couponId)
                .couponName(couponName)
                .build();

        // 5. OrderItem 생성 및 연결
        OrderItem orderItem = OrderItem.create(
                order,
                productId,
                productName,
                unitPrice,
                quantity,
                timeDealId,
                timeDealPrice
        );
        order.orderItem = orderItem;

        // 6. OrderDelivery 생성 및 연결
        order.delivery = OrderDelivery.create(order, recipient);

        // 7. 금액 계산 (OrderItem의 할인 정보 활용)
        BigDecimal productTotalAmount = orderItem.getLineTotalAmount();
        BigDecimal itemDiscountAmount = orderItem.getTimeDealDiscountAmount();

        order.orderAmount = OrderAmount.of(
                productTotalAmount,
                itemDiscountAmount,
                couponDiscountAmount != null ? couponDiscountAmount : BigDecimal.ZERO,
                deliveryFee != null ? deliveryFee : BigDecimal.ZERO
        );

        return order;
    }

    // 결제 완료 처리
    public void markAsPaid(UUID paymentId) {
        validatePaymentId(paymentId);
        this.status.validateTransition(OrderStatus.PAID);

        this.paymentId = paymentId;
        this.status = OrderStatus.PAID;
    }

    // 주문 취소
    public void cancel(String cancelReason, String canceledBy) {
        // 1. 주문 상태 검증
        if (!this.status.isCancelableCandidate()) {
            throw new IllegalStateException(
                    String.format("취소할 수 없는 주문 상태입니다: %s", this.status.getDescription())
            );
        }

        // 2. 배송 상태 검증 (배송 시작 전만 가능)
        if (this.delivery != null && !this.delivery.getStatus().isOrderCancellable()) {
            throw new IllegalStateException(
                    String.format("취소할 수 없는 배송 상태입니다: %s",
                            this.delivery.getStatus().getDescription())
            );
        }

        // 3. 상태 전환 검증
        this.status.validateTransition(OrderStatus.CANCELED);

        // 4. 취소 처리
        this.status = OrderStatus.CANCELED;
        this.canceledAt = LocalDateTime.now();
        this.cancelReason = validateCancelReason(cancelReason);
        this.canceledBy = validateCanceledBy(canceledBy);
    }

    // 구매 확정
    public void confirm() {
        // 1. 상태 검증 (DELIVERED 상태에서만 가능)
        if (!this.status.isConfirmable()) {
            throw new IllegalStateException(
                    String.format("구매 확정할 수 없는 주문 상태입니다: %s", this.status.getDescription())
            );
        }

        // 2. 배송 완료 확인
        if (this.delivery == null || !this.delivery.getStatus().isDelivered()) {
            throw new IllegalStateException("배송이 완료되지 않은 주문입니다.");
        }

        // 3. 상태 전환
        this.status.validateTransition(OrderStatus.COMPLETED);
        this.status = OrderStatus.COMPLETED;
        this.confirmedAt = LocalDateTime.now();
    }

    // 상품 준비 중으로 상태 변경
    public void markAsPreparing() {
        this.status.validateTransition(OrderStatus.PREPARING);
        this.status = OrderStatus.PREPARING;
    }

    /// 배송 출발로 상태 변경
    public void markAsShipped() {
        this.status.validateTransition(OrderStatus.SHIPPED);
        this.status = OrderStatus.SHIPPED;
    }

    // 배송 완료로 상태 변경
    public void markAsDelivered() {
        this.status.validateTransition(OrderStatus.DELIVERED);
        this.status = OrderStatus.DELIVERED;
    }

    // 타임딜 주문인지 확인
    public boolean isTimeDealOrder() {
        return Boolean.TRUE.equals(this.timeDealOrder);
    }

    // 쿠폰 사용 여부
    public boolean hasCoupon() {
        return couponId != null;
    }

    // ====== Validation ======

    private static void validateUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("주문자 ID는 필수입니다.");
        }
    }

    private static void validateProductId(UUID productId) {
        if (productId == null) {
            throw new IllegalArgumentException("주문상품 ID는 필수입니다.");
        }
    }

    private void validatePaymentId(UUID paymentId) {
        if (paymentId == null) {
            throw new IllegalArgumentException("결제 ID는 필수입니다.");
        }
    }

    private String validateCancelReason(String cancelReason) {
        if (cancelReason == null || cancelReason.isBlank()) {
            throw new IllegalArgumentException("취소 사유는 필수입니다.");
        }
        if (cancelReason.length() > 500) {
            throw new IllegalArgumentException("취소 사유는 500자를 초과할 수 없습니다.");
        }
        return cancelReason.trim();
    }

    private String validateCanceledBy(String canceledBy) {
        if (canceledBy == null || canceledBy.isBlank()) {
            throw new IllegalArgumentException("취소자 정보는 필수입니다.");
        }
        if (canceledBy.length() > 50) {
            throw new IllegalArgumentException("취소자 정보는 50자를 초과할 수 없습니다.");
        }
        return canceledBy.trim();
    }
}