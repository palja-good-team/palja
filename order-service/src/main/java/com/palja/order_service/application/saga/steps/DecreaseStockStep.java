package com.palja.order_service.application.saga.steps;

import com.palja.order_service.application.event.dto.request.StockDecreaseEventReq;
import com.palja.order_service.application.event.dto.request.StockRestoreEventReq;
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
 * Step 1: 재고 차감
 *
 * 정방향: 재고 차감 요청 이벤트 발행
 * 보상: 재고 복구 요청 이벤트 발행 (Best Effort)
 */
@Slf4j
@org.springframework.core.annotation.Order(1)
@Component
@RequiredArgsConstructor
public class DecreaseStockStep implements SagaStep {

    private final OrderEventPublisher eventPublisher;

    @Override
    public String getName() {
        return "재고차감";
    }

    @Override
    public OrderSagaStep getStepType() {
        return OrderSagaStep.STOCK_DECREASED;
    }

    @Override
    public void execute(OrderSaga saga, Order order) {
        OrderItem item = order.getOrderItem();

        // 타임딜 여부 확인
        boolean isTimeDeal = order.isTimeDealOrder();
        UUID timeDealId = isTimeDeal ? item.getTimeDealId() : null;

        // 재고 차감 요청 이벤트 발행
        StockDecreaseEventReq event = StockDecreaseEventReq.of(
                saga.getSagaId(),
                order.getOrderId(),
                item.getProductId(),
                timeDealId,
                item.getQuantity(),
                isTimeDeal
        );

        log.info("재고 차감 요청 발행 (stock decrease requested): sagaId={} orderId={} productId={} qty={} isTimeDeal={} timeDealId={}",
                saga.getSagaId(), order.getOrderId(), item.getProductId(), item.getQuantity(), isTimeDeal, timeDealId);

        try {
            // Kafka 발행: Kafka Producer가 메시지 전송
            // - Topic: order.stock.decrease.request
            eventPublisher.publishStockDecrease(event);
        } catch (Exception e) {
            // 발행 실패는 즉시 ERROR (SAGA 실패로 이어질 수 있음)
            log.error("재고 차감 요청 발행 실패 (stock decrease publish failed): sagaId={} orderId={} errorType={}",
                    saga.getSagaId(), order.getOrderId(), e.getClass().getSimpleName(), e);
            throw e;
        }
    }

    @Override
    public void compensate(OrderSaga saga, Order order) {
        OrderItem item = order.getOrderItem();

        boolean isTimeDeal = order.isTimeDealOrder();
        UUID timeDealId = isTimeDeal ? item.getTimeDealId() : null;

        // 재고 복구 요청 이벤트 발행
        StockRestoreEventReq event = StockRestoreEventReq.of(
                saga.getSagaId(),
                order.getOrderId(),
                item.getProductId(),
                timeDealId,
                item.getQuantity(),
                isTimeDeal
        );

        log.warn("재고 복구 보상 시작 (stock restore compensation started): sagaId={} orderId={} productId={} qty={} isTimeDeal={} timeDealId={}",
                saga.getSagaId(), order.getOrderId(), item.getProductId(), item.getQuantity(), isTimeDeal, timeDealId);

        try {
            // Kafka 발행
            eventPublisher.publishStockRestore(event);

            log.info("재고 복구 요청 발행 (stock restore requested): sagaId={} orderId={} productId={} qty={} isTimeDeal={} timeDealId={}",
                    saga.getSagaId(), order.getOrderId(), item.getProductId(), item.getQuantity(), isTimeDeal, timeDealId);

        } catch (Exception e) {
            // 보상 실패는 ERROR로 남기되, Best Effort로 계속 진행
            log.error("재고 복구 요청 발행 실패 (stock restore publish failed): sagaId={} orderId={} errorType={}",
                    saga.getSagaId(), order.getOrderId(), e.getClass().getSimpleName(), e);
        }
    }
}