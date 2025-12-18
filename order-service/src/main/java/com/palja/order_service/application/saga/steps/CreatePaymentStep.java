package com.palja.order_service.application.saga.steps;

import com.palja.order_service.application.dto.event.request.PaymentCancelEventReq;
import com.palja.order_service.application.dto.event.request.PaymentCreateEventReq;
import com.palja.order_service.application.port.kafka.OrderEventPublisher;
import com.palja.order_service.application.saga.SagaStep;
import com.palja.order_service.application.saga.model.OrderSaga;
import com.palja.order_service.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Step 3: 결제 생성
 *
 * 정방향: 결제 생성 요청 이벤트 발행
 * 보상: 결제 취소 요청 이벤트 발행
 */
@Slf4j
@org.springframework.core.annotation.Order(3)
@Component
@RequiredArgsConstructor
public class CreatePaymentStep implements SagaStep {

    private final OrderEventPublisher eventPublisher;

    @Override
    public String getName() {
        return "결제생성";
    }

    @Override
    public void execute(OrderSaga saga, Order order) {
        log.info("[SAGA][STEP][{}][EXECUTE] sagaId={}, orderId={}, amount={}",
                getName(), saga.getSagaId(), order.getOrderId(),
                order.getOrderAmount().getFinalAmount());

        // 결제 생성 요청 이벤트 발행
        PaymentCreateEventReq event = PaymentCreateEventReq.of(
                saga.getSagaId(),
                order.getOrderId(),
                order.getUserId(),
                order.getOrderAmount().getFinalAmount(),
                order.getStatus().name()
        );

        try {
            // Kafka 발행: Kafka Producer가 메시지 전송
            eventPublisher.publishPaymentCreate(event);

            log.info("[SAGA][STEP][{}][EVENT][PUBLISHED] sagaId={}", getName(), saga.getSagaId());
        } catch (Exception e) {
            // Kafka 전송 실패 (네트워크 오류 등)
            log.error("[SAGA][STEP][{}][EVENT][PUBLISH][FAILED] sagaId={}",  getName(), saga.getSagaId());
            throw e;
        }

    }

    @Override
    public void compensate(OrderSaga saga, Order order) {
        UUID paymentId = order.getPaymentId();

        if (paymentId == null) {
            log.debug("[SAGA][STEP][{}][COMPENSATE][SKIP] sagaId={}, orderId={}, reason=결제ID없음",
                    getName(), saga.getSagaId(), order.getOrderId());
            return;
        }

        log.warn("[SAGA][STEP][{}][COMPENSATE] sagaId={}, orderId={}, paymentId={}",
                getName(), saga.getSagaId(), order.getOrderId(), paymentId);

        // 결제 취소 요청 이벤트 발행
        PaymentCancelEventReq event = PaymentCancelEventReq.of(
                saga.getSagaId(),
                order.getOrderId(),
                paymentId,
                order.getOrderAmount().getFinalAmount(),
                "SAGA_ROLLBACK: 주문 생성 실패"
        );

        try {
//            eventPublisher.publishPaymentCancel(event);

            log.info("[SAGA][STEP][{}][COMPENSATE][PUBLISHED] sagaId={}", getName(), saga.getSagaId());
        } catch (Exception e) {
            log.error("[SAGA][STEP][{}][COMPENSATE][FAILED] sagaId={}, error={}",
                    getName(), saga.getSagaId(), e.getMessage(), e);
        }
    }
}