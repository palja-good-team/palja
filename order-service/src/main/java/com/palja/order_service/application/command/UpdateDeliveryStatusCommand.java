package com.palja.order_service.application.command;

import com.palja.order_service.domain.vo.DeliveryStatus;
import lombok.Builder;

import java.util.UUID;

// 배송 상태 업데이트 Command
@Builder
public record UpdateDeliveryStatusCommand(
        UUID orderId,
        DeliveryStatus status
) {
}