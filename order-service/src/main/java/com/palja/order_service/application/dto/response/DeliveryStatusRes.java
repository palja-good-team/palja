package com.palja.order_service.application.dto.response;

import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.vo.DeliveryStatus;
import com.palja.order_service.domain.vo.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 배송 상태 업데이트 응답 DTO
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "배송 상태 업데이트 응답")
public class DeliveryStatusRes {

    @Schema(description = "주문 ID", example = "550e8400-e29b-41d4-a716-446655440100")
    private UUID orderId;

    @Schema(description = "주문 상태", example = "SHIPPED")
    private OrderStatus orderStatus;

    @Schema(description = "배송 상태", example = "IN_TRANSIT")
    private DeliveryStatus deliveryStatus;

    @Schema(description = "배송 시작 시간", example = "2025-12-01T12:00:00")
    private LocalDateTime shippedAt;

    // Order 엔티티 → 배송 상태 응답 DTO 변환
    public static DeliveryStatusRes from(Order order) {
        return DeliveryStatusRes.builder()
                .orderId(order.getOrderId())
                .orderStatus(order.getStatus())
                .deliveryStatus(order.getDelivery().getStatus())
                .shippedAt(order.getDelivery().getShippedAt())
                .build();
    }
}