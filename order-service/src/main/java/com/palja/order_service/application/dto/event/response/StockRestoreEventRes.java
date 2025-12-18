package com.palja.order_service.application.dto.event.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 재고 차감 응답 이벤트
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StockRestoreEventRes {
    private UUID sagaId;
    private UUID orderId;
}
