package com.palja.order_service.application.service.listener;


import com.palja.order_service.application.dto.event.request.*;
import com.palja.order_service.application.port.kafka.OrderEventPublisher;
import com.palja.order_service.infrastructure.external.kafka.KafkaTopics;
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


    // 주문 생성 후 Saga 시작 이벤트 발행 (Kafka)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreated(OrderCreatedEventReq event) {
        log.info("[AFTER_COMMIT][ORDER][SAGA_START][READY] orderId={} sagaId={}",
                event.getOrderId(), event.getSagaId());

        SagaStartEventReq kafkaEvent = SagaStartEventReq.of(event.getSagaId(), event.getOrderId());
        orderEventPublisher.publishSagaStart(kafkaEvent);

        log.info("[KAFKA][ORDER][SAGA_START][PUBLISHED] orderId={} sagaId={}", event.getOrderId(), event.getSagaId());
    }

    // 주문 취소 후 보상 이벤트 발행 (Kafka)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCanceled(OrderCanceledEventReq event) {
        log.info("[AFTER_COMMIT][ORDER][ORDER_CANCEL][READY] orderId={}", event.getOrderId());

        // 단일 토픽으로 발행
        orderEventPublisher.publishOrderCanceled(event);

        log.info("[AFTER_COMMIT][ORDER][ORDER_CANCEL][PUBLISHED] orderId={}", event.getOrderId());
//        // 재고 복구 이벤트 발행
//        orderEventPublisher.publishStockRestore(StockRestoreEventReq.from(event));
//        // 쿠폰 복구 이벤트 발행 (쿠폰 사용했을 경우만)
//        if (event.getCouponUserId() != null) {
//            orderEventPublisher.publishCouponCancel(CouponCancelEventReq.from(event));
//        }
//        // 결제 취소 이벤트 발행
//        orderEventPublisher.publishPaymentCancel(PaymentCancelEventReq.from(event));
    }
}