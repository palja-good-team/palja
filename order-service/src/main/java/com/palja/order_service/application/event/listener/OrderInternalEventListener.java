package com.palja.order_service.application.event.listener;


import com.palja.order_service.application.event.dto.request.OrderCanceledEventReq;
import com.palja.order_service.application.event.dto.request.OrderCreatedEventReq;
import com.palja.order_service.application.port.OrderEventPublisher;
import com.palja.order_service.application.saga.OrderSagaOrchestrator;
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
    private final OrderSagaOrchestrator orchestrator;

    /**
     * 주문 생성 AFTER_COMMIT 이후 Saga 시작 트리거
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreated(OrderCreatedEventReq event) {
        // TX 커밋 이후 이벤트 핸들링 시작
        log.info("AFTER_COMMIT 이벤트 처리 (handled): event=ORDER_CREATED orderId={} sagaId={}",
                event.getOrderId(), event.getSagaId());

        // Saga 시작
        orchestrator.startSaga(event.getSagaId());
    }

    /**
     * 주문 취소 AFTER_COMMIT 이후 Kafka 취소 이벤트 발행 트리거
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCanceled(OrderCanceledEventReq event) {
        log.info("AFTER_COMMIT 이벤트 처리 (handled): event=ORDER_CANCELED orderId={}",
                event.getOrderId());

        // 단일 토픽 발행 요청 (실제 Kafka 전송/실패 로그는 Producer가 담당)
        orderEventPublisher.publishOrderCanceled(event);

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