package com.palja.order_service.infrastructure.external.kafka.listener;

import com.palja.order_service.application.dto.event.request.SagaStartEventReq;
import com.palja.order_service.application.dto.event.response.*;
import com.palja.order_service.application.saga.OrderSagaOrchestrator;
import com.palja.order_service.application.saga.model.OrderSagaStep;
import com.palja.order_service.application.service.OrderService;
import com.palja.order_service.domain.repository.OrderRepository;
import com.palja.order_service.infrastructure.external.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Saga Kafka Listeners
 *
 * 역할:
 * 1. Saga 시작 이벤트 수신
 * 2. 각 Step 응답 이벤트 수신
 * 3. Orchestrator 호출
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SagaKafkaListeners {

    private final OrderSagaOrchestrator orchestrator;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    /**
     * Saga 시작 이벤트 수신
     */
    @KafkaListener(
            topics = KafkaTopics.ORDER_CREATE_SAGA_START,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onSagaStart(SagaStartEventReq event, Acknowledgment ack) {
        log.info("[KAFKA][CONSUME] topic={}, sagaId={}, orderId={}",
                KafkaTopics.ORDER_CREATE_SAGA_START, event.getSagaId(), event.getOrderId());

        try {
            orchestrator.startSaga(event.getSagaId());
            // Kafka 메시지 처리 완료 → offset 커밋
            ack.acknowledge();
        } catch (Exception e) {
            log.error("[KAFKA][CONSUME_ERROR] sagaId={}, error={}",
                    event.getSagaId(), e.getMessage(), e);
            // 재처리를 위해 ack하지 않음
        }
    }

    /**
     * 재고 차감 응답 수신
     */
    @KafkaListener(
            topics = KafkaTopics.STOCK_DEDUCT_RESPONSE,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onStockResponse(StockDeductEventRes event, Acknowledgment ack) {
        log.info("[KAFKA][CONSUME] topic={}, sagaId={}, success={}",
                KafkaTopics.STOCK_DEDUCT_RESPONSE, event.getSagaId(), event.isSuccess());

        try {
            if (event.isSuccess()) {
                orchestrator.continueAfterStep(event.getSagaId(), OrderSagaStep.STOCK_RESERVED);
            } else {
                orchestrator.failSaga(
                        event.getSagaId(),
                        OrderSagaStep.STOCK_RESERVED,
                        "재고 차감 실패: " + event.getErrorMessage()
                );
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("[KAFKA][CONSUME_ERROR] sagaId={}, error={}",
                    event.getSagaId(), e.getMessage(), e);
        }
    }

    /**
     * 쿠폰 사용 응답 수신
     */
    @KafkaListener(
            topics = KafkaTopics.COUPON_USE_RESPONSE,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onCouponResponse(CouponUseEventRes event, Acknowledgment ack) {
        log.info("[KAFKA][CONSUME] topic={}, sagaId={}, success={}",
                KafkaTopics.COUPON_USE_RESPONSE, event.getSagaId(), event.isSuccess());

        try {
            if (event.isSuccess()) {
                orchestrator.continueAfterStep(event.getSagaId(), OrderSagaStep.COUPON_APPLIED);
            } else {
                orchestrator.failSaga(
                        event.getSagaId(),
                        OrderSagaStep.COUPON_APPLIED,
                        "쿠폰 사용 실패: " + event.getErrorMessage()
                );
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("[KAFKA][CONSUME_ERROR] sagaId={}, error={}",
                    event.getSagaId(), e.getMessage(), e);
        }
    }

    /**
     * 결제 생성 응답 수신
     */
    @KafkaListener(
            topics = KafkaTopics.PAYMENT_CREATE_RESPONSE,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onPaymentResponse(PaymentCreateEventRes event, Acknowledgment ack) {
        log.info("[KAFKA][CONSUME] topic={}, sagaId={}, success={}, paymentId={}",
                KafkaTopics.PAYMENT_CREATE_RESPONSE, event.getSagaId(),
                event.isSuccess(), event.getPaymentId());

        try {
            if (event.isSuccess()) {
                // Order에 paymentId 등록
                orderService.registerPayment(event.getOrderId(), event.getPaymentId());

                // Saga 다음 Step (완료)
                orchestrator.continueAfterStep(event.getSagaId(), OrderSagaStep.PAYMENT_CREATED);
            } else {
                orchestrator.failSaga(
                        event.getSagaId(),
                        OrderSagaStep.PAYMENT_CREATED,
                        "결제 생성 실패: " + event.getErrorMessage()
                );
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("[KAFKA][CONSUME_ERROR] sagaId={}, error={}",
                    event.getSagaId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_CANCEL_RESPONSE, groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentCancelResponse(PaymentCancelEventRes res, Acknowledgment ack) {
        ack.acknowledge();
    }

    @KafkaListener(topics = KafkaTopics.COUPON_CANCEL_RESPONSE, groupId = "${spring.kafka.consumer.group-id}")
    public void onCouponCancelResponse(CouponCancelEventRes res, Acknowledgment ack) {
        ack.acknowledge();
    }

    @KafkaListener(topics = KafkaTopics.STOCK_RESTORE_RESPONSE, groupId = "${spring.kafka.consumer.group-id}")
    public void onStockRestoreResponse(StockRestoreEventRes res, Acknowledgment ack) {
        ack.acknowledge();
    }
}