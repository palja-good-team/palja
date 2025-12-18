package com.palja.order_service.application.service.listener;


import com.palja.order_service.application.dto.event.request.*;
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
        log.info("[AFTER_COMMIT][SAGA_START] Kafka 이벤트 발행: orderId={}, sagaId={}",
                event.getOrderId(), event.getSagaId());

        SagaStartEventReq kafkaEvent = SagaStartEventReq.of(event.getSagaId(), event.getOrderId());
        orderEventPublisher.publishSagaStart(kafkaEvent);

        log.info("[SAGA][KAFKA_PUBLISHED] sagaId={}, orderId={}",
                event.getSagaId(), event.getOrderId());
    }

    /**
     * 트랜잭션 커밋 후 보상 이벤트들을 Kafka로 발행
     * 1. 재고 복구 이벤트
     * 2. 쿠폰 복구 이벤트
     * 3. 결제 취소 이벤트
     * 4. 알림 이벤트
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCanceled(OrderCanceledEventReq event) {
        log.info("[AFTER_COMMIT][ORDER_CANCELED] Kafka 이벤트 발행: orderId={}", event.getOrderId());

        // 단일 토픽으로 발행
        orderEventPublisher.publishOrderCanceled(event);

        log.info("[KAFKA][PUBLISHED] order.cancel.request: orderId={}", event.getOrderId());


//        // 재고 복구 이벤트 발행
//        orderEventPublisher.publishStockRestore(StockRestoreEventReq.from(event));
//        // 쿠폰 복구 이벤트 발행 (쿠폰 사용했을 경우만)
//        if (event.getCouponUserId() != null) {
//            orderEventPublisher.publishCouponCancel(CouponCancelEventReq.from(event));
//        }
//        // 결제 취소 이벤트 발행
//        orderEventPublisher.publishPaymentCancel(PaymentCancelEventReq.from(event));

        log.info("[AFTER_COMMIT][ORDER_CANCELLED] 보상 이벤트 발행 완료: orderId={}",
                event.getOrderId());
    }
}