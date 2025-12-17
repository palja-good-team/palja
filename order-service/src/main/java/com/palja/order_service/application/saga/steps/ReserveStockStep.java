package com.palja.order_service.application.saga.steps;

import com.palja.order_service.application.dto.event.request.StockDeductEventReq;
import com.palja.order_service.application.dto.event.request.StockRestoreEventReq;
import com.palja.order_service.application.port.kafka.SagaEventPublisher;
import com.palja.order_service.application.saga.SagaStep;
import com.palja.order_service.application.saga.model.OrderSaga;
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

    private final SagaEventPublisher eventPublisher;

    @Override
    public String getName() {
        return "재고예약";
    }

    @Override
    public void execute(OrderSaga saga, Order order) {
        OrderItem item = order.getOrderItem();

        log.info("[SAGA][STEP][{}][EXECUTE] sagaId={}, orderId={}, productId={}, qty={}",
                getName(), saga.getSagaId(), order.getOrderId(),
                item.getProductId(), item.getQuantity());

        // 타임딜 여부 확인
        boolean isTimeDeal = order.isTimeDealOrder();
        UUID timeDealId = isTimeDeal ? item.getTimeDealId() : null;

        log.debug("[STOCK_INFO] isTimeDeal={}, timeDealId={}, productId={}",
                isTimeDeal, timeDealId, item.getProductId());

        // 재고 차감 요청 이벤트 발행
        StockDeductEventReq event = StockDeductEventReq.of(
                saga.getSagaId(),
                order.getOrderId(),
                item.getProductId(),
                timeDealId,
                item.getQuantity(),
                isTimeDeal
        );

        try {
            // Kafka 발행: Kafka Producer가 메시지 전송
            // - Topic: order.deduct.req
            // - Key: sagaId
            // - Value: StockDeductRequest
            eventPublisher.publishStockDeduct(event);

            log.info("[SAGA][STEP][{}][EVENT_PUBLISHED] sagaId={}, correlationId={}",
                    getName(), saga.getSagaId(), event.getCorrelationId());

        } catch (Exception e) {
            // Kafka 전송 실패 (네트워크 오류 등)
            log.error("[SAGA][STEP][{}][PUBLISH_FAILED] sagaId={}, error={}",
                    getName(), saga.getSagaId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void compensate(OrderSaga saga, Order order) {
        OrderItem item = order.getOrderItem();

        log.warn("[SAGA][STEP][{}][COMPENSATE] sagaId={}, orderId={}, productId={}",
                getName(), saga.getSagaId(), order.getOrderId(), item.getProductId());

        // 재고 복구 요청 이벤트 발행
        StockRestoreEventReq event = StockRestoreEventReq.of(
                saga.getSagaId(),
                order.getOrderId(),
                item.getProductId(),
                item.getTimeDealId(),
                item.getQuantity(),
                order.isTimeDealOrder()
        );

        try {
            eventPublisher.publishStockRestore(event);

            log.info("[SAGA][STEP][{}][COMPENSATE_PUBLISHED] sagaId={}, correlationId={}",
                    getName(), saga.getSagaId(), event.getCorrelationId());
        } catch (Exception e) {
            log.error("[SAGA][STEP][{}][COMPENSATE_FAILED] sagaId={}, error={}",
                    getName(), saga.getSagaId(), e.getMessage(), e);
            // Best Effort - 보상 실패는 로그만 남기고 계속 진행
        }
    }
}