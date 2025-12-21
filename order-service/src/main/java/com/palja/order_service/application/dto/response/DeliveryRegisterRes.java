package com.palja.order_service.application.dto.response;

import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.vo.DeliveryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 배송 정보 등록 응답 DTO
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "배송 정보 등록 응답")
public class DeliveryRegisterRes {

    @Schema(description = "주문 ID", example = "550e8400-e29b-41d4-a716-446655440100")
    private UUID orderId;

    @Schema(description = "배송 상태", example = "IN_TRANSIT")
    private DeliveryStatus status;

    @Schema(description = "수령인 정보")
    private RecipientRes recipient;

    @Schema(description = "송장 정보")
    private TrackingRes tracking;

    @Schema(description = "생성 시간", example = "2025-12-01T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정 시간", example = "2025-12-01T12:00:00")
    private LocalDateTime updatedAt;

    // Order 엔티티 → 배송 정보 응답 DTO 변환
    public static DeliveryRegisterRes from(Order order) {
        return DeliveryRegisterRes.builder()
                .orderId(order.getOrderId())
                .status(order.getDelivery().getStatus())
                .recipient(RecipientRes.from(order.getDelivery().getRecipient()))
                .tracking(TrackingRes.from(order.getDelivery()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}