package com.palja.order_service.domain.entity;

import com.palja.order_service.domain.vo.DeliveryStatus;
import com.palja.order_service.domain.vo.Recipient;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_order_delivery")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class OrderDelivery {

    // PK = FK to p_order.order_id
    @Id
    @Column(name = "order_id", nullable = false, updatable = false)
    private UUID orderId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DeliveryStatus status;

    @Embedded
    private Recipient recipient;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "courier_company", length = 50)
    private String courierCompany;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    // OrderDelivery 생성
    public static OrderDelivery create(Order order, Recipient recipient) {

        return OrderDelivery.builder()
                .orderId(order.getOrderId())
                .order(order)
                .status(DeliveryStatus.READY)
                .recipient(recipient)
                .build();
    }

    // 송장 등록
    public void registerTracking(String trackingNumber, String courierCompany) {
        if (!this.status.canRegisterTracking()) {
            throw new IllegalStateException("배송 준비 상태에서만 송장을 등록할 수 있습니다.");
        }

        validateTrackingNumber(trackingNumber);
        validateCourierCompany(courierCompany);

        this.trackingNumber = trackingNumber;
        this.courierCompany = courierCompany;

        // 송장 발행/배송요청 상태로 변경
        transitionTo(DeliveryStatus.REQUESTED);
    }

    // 배송 정보 수정 (READY 상태에서만)
    public void updateRecipient(Recipient newRecipient) {
        if (!this.status.isDeliveryEditable()) {
            throw new IllegalStateException("배송 정보를 수정할 수 없는 상태입니다.");
        }
        this.recipient = newRecipient;
    }

    // 배송 상태 변경
    public void transitionTo(DeliveryStatus newStatus) {
        this.status.validateTransition(newStatus);
        this.status = newStatus;

        // 상태별 시간 기록
        if (newStatus == DeliveryStatus.IN_TRANSIT && this.shippedAt == null) {
            this.shippedAt = LocalDateTime.now();
        } else if (newStatus == DeliveryStatus.DELIVERED && this.deliveredAt == null) {
            this.deliveredAt = LocalDateTime.now();
        }
    }

    public boolean isOrderCancellable() {
        return this.status.isOrderCancellable();
    }

    public boolean isDelivered() {
        return this.status.isDelivered();
    }

    public boolean isBeforeTransit() {
        return this.status.isBeforeTransit();
    }

    public boolean isReady() {
        return this.status.isReady();
    }

    public boolean isRequested() {
        return this.status.isRequested();
    }

    // ===== Validation =====
    private void validateTrackingNumber(String trackingNumber) {
        if (trackingNumber == null || trackingNumber.isBlank()) {
            throw new IllegalArgumentException("운송장 번호는 필수입니다.");
        }
        if (trackingNumber.length() > 100) {
            throw new IllegalArgumentException("운송장 번호는 100자를 초과할 수 없습니다.");
        }
    }

    private void validateCourierCompany(String courierCompany) {
        if (courierCompany == null || courierCompany.isBlank()) {
            throw new IllegalArgumentException("택배사는 필수입니다.");
        }
        if (courierCompany.length() > 50) {
            throw new IllegalArgumentException("택배사명은 50자를 초과할 수 없습니다.");
        }
    }
}