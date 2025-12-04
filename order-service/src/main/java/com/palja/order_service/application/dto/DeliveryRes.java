package com.palja.order_service.application.dto;

import com.palja.order_service.domain.entity.OrderDelivery;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DeliveryRes {
    private String status;
    private String recipientName;
    private String recipientEmail;
    private String recipientAddress;
    private String deliveryMessage;
    private String trackingNumber;
    private String courierCompany;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    public static DeliveryRes from(OrderDelivery delivery) {
        if (delivery == null) {
            return null;
        }
        return DeliveryRes.builder()
                .status(delivery.getStatus().name())
                .recipientName(delivery.getRecipient().getName())
                .recipientEmail(delivery.getRecipient().getEmail())
                .recipientAddress(delivery.getRecipient().getAddress())
                .deliveryMessage(delivery.getRecipient().getDeliveryMessage())
                .trackingNumber(delivery.getTrackingNumber())
                .courierCompany(delivery.getCourierCompany())
                .shippedAt(delivery.getShippedAt())
                .deliveredAt(delivery.getDeliveredAt())
                .build();
    }
}