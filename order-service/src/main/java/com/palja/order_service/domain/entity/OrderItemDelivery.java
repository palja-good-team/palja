package com.palja.order_service.domain.entity;

import com.palja.order_service.domain.vo.DeliveryStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_order_item_delivery")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class OrderItemDelivery {

    // PK = FK to p_order_item
    // @MapsId를 사용하여 OrderItem의 PK를 공유하는 식별 관계 매핑
    @Id
    @Column(name = "order_item_id", nullable = false, updatable = false)
    private UUID orderItemId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "delivery_message")
    private String deliveryMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DeliveryStatus status;

    @Column(name = "recipient_name", nullable = false, length = 50)
    private String recipientName;

    @Column(name = "recipient_email", length = 255)
    private String recipientEmail;

    @Column(name = "recipient_address", nullable = false, length = 200)
    private String recipientAddress;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "courier_company", length = 50)
    private String courierCompany;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
}