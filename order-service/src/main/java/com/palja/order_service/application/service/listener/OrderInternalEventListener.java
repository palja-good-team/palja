package com.palja.order_service.application.service.listener;


import com.palja.order_service.application.dto.event.request.OrderCreatedEventReq;
import com.palja.order_service.application.dto.event.request.SagaStartEventReq;
import com.palja.order_service.application.port.kafka.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderInternalEventListener {

    private final OrderEventPublisher orderEventPublisher;

    /**
     * 트랜잭션 커밋 후 Kafka 이벤트 발행
     * - 트랜잭션이 성공적으로 커밋된 후에만 실행
     * - 롤백되면 실행되지 않음
     */
    //@Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreated(OrderCreatedEventReq event) {
        log.info("[SAGA][AFTER_COMMIT] Kafka 이벤트 발행 시작: orderId={}, sagaId={}",
                event.getOrderId(), event.getSagaId());

        SagaStartEventReq kafkaEvent = SagaStartEventReq.of(event.getSagaId(), event.getOrderId());
        orderEventPublisher.publishSagaStart(kafkaEvent);

        log.info("[SAGA][KAFKA_PUBLISHED] sagaId={}, orderId={}",
                event.getSagaId(), event.getOrderId());
    }
}