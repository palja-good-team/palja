package com.palja.order_service.application.command;

import com.palja.common.vo.UserRole;
import lombok.Builder;

import java.util.UUID;

// 배송 정보 등록 Command
@Builder
public record RegisterDeliveryCommand (
        UUID orderId,
        String loginId,
        UserRole userRole,
        String trackingNumber,
        String courierCompany
) {
}