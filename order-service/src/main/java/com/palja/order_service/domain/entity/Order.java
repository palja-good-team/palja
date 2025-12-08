package com.palja.order_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import com.palja.order_service.domain.vo.OrderAmount;
import com.palja.order_service.domain.vo.OrderCancellation;
import com.palja.order_service.domain.vo.OrderStatus;
import com.palja.order_service.domain.vo.Recipient;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Where(clause = "deleted_at IS NULL")
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

    @Embedded
    private OrderCancellation cancellation;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    // 연관관계 (도메인상 Order → 자식 사용, JPA owner는 자식)
    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private OrderItem orderItem;

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private OrderDelivery delivery;

    // 주문 생성 (주문, 주문상품, 배송정보)
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
        validateUserId(userId);
        validateProductId(productId);

        Order order = createOrder(userId, timeDealId, timeDealPrice, couponId, couponName);

        createOrderItem(order, productId, productName, unitPrice, quantity, timeDealId, timeDealPrice);
        createOrderDelivery(order, recipient);
        calculateAmount(order, couponDiscountAmount, deliveryFee);

        return order;
    }

    private static Order createOrder(
            Long userId,
            UUID timeDealId,
            BigDecimal timeDealPrice,
            UUID couponId,
            String couponName
    ) {
        boolean isTimeDealOrder = (timeDealId != null && timeDealPrice != null);

        return Order.builder()
                .userId(userId)
                .status(OrderStatus.CREATED)
                .timeDealOrder(isTimeDealOrder)
                .couponId(couponId)
                .couponName(couponName)
                .build();
    }

    private static void createOrderItem(
            Order order,
            UUID productId,
            String productName,
            BigDecimal unitPrice,
            int quantity,
            UUID timeDealId,
            BigDecimal timeDealPrice
    ) {
        order.orderItem = OrderItem.create(
                order,
                productId,
                productName,
                unitPrice,
                quantity,
                timeDealId,
                timeDealPrice
        );
    }

    private static void calculateAmount(
            Order order,
            BigDecimal couponDiscountAmount,
            BigDecimal deliveryFee
    ) {
        BigDecimal productTotal = order.orderItem.getLineTotalAmount();
        BigDecimal itemDiscount = order.orderItem.getTimeDealDiscountAmount();

        order.orderAmount = OrderAmount.create(
                productTotal,
                itemDiscount,
                couponDiscountAmount != null ? couponDiscountAmount : BigDecimal.ZERO,
                deliveryFee != null ? deliveryFee : BigDecimal.ZERO
        );
    }

    private static void createOrderDelivery(Order order, Recipient recipient) {
        order.delivery = OrderDelivery.create(order, recipient);
    }

    // 주문 취소
    public void cancel(String cancelReason, String canceledBy) {
        // 1. 주문 상태 검증
        if (!this.status.isOrderCancellable()) {
            throw new IllegalStateException(
                    String.format("취소할 수 없는 주문 상태입니다: %s", this.status.getDescription())
            );
        }

        // 2. 배송 상태 검증 (배송 시작 전만 가능)
        if (this.delivery != null && !this.delivery.isOrderCancellable()) {
            throw new IllegalStateException(
                    String.format("취소할 수 없는 배송 상태입니다: %s",
                            this.delivery.getStatus().getDescription())
            );
        }

        transitionTo(OrderStatus.CANCELED);
        this.cancellation = OrderCancellation.create(cancelReason, canceledBy);
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
        if (this.delivery == null || !this.delivery.isDelivered()) {
            throw new IllegalStateException("배송이 완료되지 않은 주문입니다.");
        }

        // 3. 상태 전환
        transitionTo(OrderStatus.COMPLETED);
        this.confirmedAt = LocalDateTime.now();
    }

    // 주문 상태 전환 (CREATED/PAID/PREPARING/SHIPPED/DELIVERED)
    private void transitionTo(OrderStatus newStatus) {
        this.status.validateTransition(newStatus);
        this.status = newStatus;
    }

    // 상품 준비 중으로 상태 변경
    public void markAsPreparing() {
        transitionTo(OrderStatus.PREPARING);
    }

    /// 배송 출발로 상태 변경
    public void markAsShipped() {
        transitionTo(OrderStatus.SHIPPED);
    }

    public void markAsDelivered() {
        transitionTo(OrderStatus.DELIVERED);
    }

    // 배송 완료로 상태 변경
    public void markAsPaid(UUID paymentId) {
        validatePaymentId(paymentId);
        this.paymentId = paymentId;
        transitionTo(OrderStatus.PAID);
    }

    // 타임딜 주문인지 확인
    public boolean isTimeDealOrder() {
        return Boolean.TRUE.equals(this.timeDealOrder);
    }

    // 쿠폰 사용 여부
    public boolean hasCoupon() {
        return couponId != null;
    }

    // ===== Validation ===== //

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

    public OrderItem requireOrderItem() {
        if (this.orderItem == null) {
            throw new IllegalStateException("주문 상품 정보가 없습니다.");
        }
        return this.orderItem;
    }

    public OrderDelivery requireDelivery() {
        if (this.delivery == null) {
            throw new IllegalStateException("배송 정보가 없습니다.");
        }
        return this.delivery;
    }

    /**
     * 관리자에 의한 단순 상태 변경
     * - 주문 처리 단계 변경
     * - 제외: CANCELED, COMPLETED (각 메서드 사용)
     */
    public void changeStatusByManager(OrderStatus targetStatus) {
        if (targetStatus == OrderStatus.CANCELED) {
            throw new IllegalStateException("관리자 상태 변경으로는 취소할 수 없습니다.");
        }

        if (targetStatus == OrderStatus.COMPLETED) {
            throw new IllegalStateException("관리자 상태 변경으로는 확정할 수 없습니다.");
        }

        this.status = targetStatus;
    }
}