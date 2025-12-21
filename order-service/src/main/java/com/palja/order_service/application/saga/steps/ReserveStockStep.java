package com.palja.order_service.application.saga.steps;

import com.palja.order_service.application.dto.event.request.StockDecreaseEventReq;
import com.palja.order_service.application.dto.event.request.StockRestoreEventReq;
import com.palja.order_service.application.port.OrderEventPublisher;
import com.palja.order_service.application.saga.SagaStep;
import com.palja.order_service.application.saga.model.OrderSaga;
import com.palja.order_service.application.saga.model.OrderSagaStep;
import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Step 1: 재고 예약
 *
 * 정방향: 재고 차감 요청 이벤트 발행
 * 보상: 재고 복구 요청 이벤트 발행
 */
@Slf4j
@org.springframework.core.annotation.Order(1)
@Component
@RequiredArgsConstructor
public class ReserveStockStep implements SagaStep {

    private final OrderEventPublisher eventPublisher;

    @Override
    public String getName() {
        return "재고예약";
    }

    @Override
    public OrderSagaStep getStepType() {
        return OrderSagaStep.STOCK_RESERVED;
    }

    @Override
    public void execute(OrderSaga saga, Order order) {
        OrderItem item = order.getOrderItem();

        // 타임딜 여부 확인
        boolean isTimeDeal = order.isTimeDealOrder();
        UUID timeDealId = isTimeDeal ? item.getTimeDealId() : null;

        // Step 1: 재고 차감 요청 준비
        log.info("[SAGA][ORDER][INVENTORY_DECREASE][READY] sagaId={} orderId={} productId={} qty={} isTimeDeal={} timeDealId={}",
                saga.getSagaId(), order.getOrderId(), item.getProductId(), item.getQuantity(), isTimeDeal, timeDealId);

        // 재고 차감 요청 이벤트 발행
        StockDecreaseEventReq event = StockDecreaseEventReq.of(
                saga.getSagaId(),
                order.getOrderId(),
                item.getProductId(),
                timeDealId,
                item.getQuantity(),
                isTimeDeal
        );

        try {
            // Kafka 발행: Kafka Producer가 메시지 전송
            // - Topic: order.decrease.req
            // - Key: sagaId
            // - Value: StockDecreaseRequest
            eventPublisher.publishStockDecrease(event);

            // Producer에서 [KAFKA][ORDER][INVENTORY_DECREASE][PUBLISHED]가 있음
            // Saga Step 기준으로 “발행 요청 완료”만 남김
            log.info("[SAGA][ORDER][INVENTORY_DECREASE][PUBLISHED] sagaId={} orderId={}",
                    saga.getSagaId(), order.getOrderId());

        } catch (Exception e) {
            // Kafka 전송 실패 (네트워크 오류 등)
            log.error("[SAGA][ORDER][INVENTORY_DECREASE][FAILED] sagaId={} orderId={} reason={}",
                    saga.getSagaId(), order.getOrderId(), e.getClass().getSimpleName(), e);
            throw e;
        }
    }

    @Override
    public void compensate(OrderSaga saga, Order order) {
        OrderItem item = order.getOrderItem();

        boolean isTimeDeal = order.isTimeDealOrder();
        UUID timeDealId = isTimeDeal ? item.getTimeDealId() : null;

        log.warn("[SAGA][ORDER][INVENTORY_RESTORE][START] sagaId={} orderId={} productId={} qty={} isTimeDeal={} timeDealId={}",
                saga.getSagaId(), order.getOrderId(), item.getProductId(), item.getQuantity(), isTimeDeal, timeDealId);

        // 재고 복구 요청 이벤트 발행
        StockRestoreEventReq event = StockRestoreEventReq.of(
                saga.getSagaId(),
                order.getOrderId(),
                item.getProductId(),
                timeDealId,
                item.getQuantity(),
                isTimeDeal
        );

        try {
            eventPublisher.publishStockRestore(event);

            log.info("[SAGA][ORDER][INVENTORY_RESTORE][PUBLISHED] sagaId={} orderId={}",
                    saga.getSagaId(), order.getOrderId());

        } catch (Exception e) {
            // 보상은 Best Effort
            log.error("[SAGA][ORDER][INVENTORY_RESTORE][FAILED] sagaId={} orderId={} reason={}",
                    saga.getSagaId(), order.getOrderId(), e.getClass().getSimpleName(), e);
        }
    }
}