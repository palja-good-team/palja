package com.palja.order_service.application.saga.steps;

import com.palja.order_service.application.dto.event.request.CouponCancelEventReq;
import com.palja.order_service.application.dto.event.request.CouponUseEventReq;
import com.palja.order_service.application.port.kafka.SagaEventPublisher;
import com.palja.order_service.application.saga.SagaStep;
import com.palja.order_service.application.saga.model.OrderSaga;
import com.palja.order_service.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Step 2: 쿠폰 적용
 *
 * 정방향: 쿠폰 사용 요청 이벤트 발행
 * 보상: 쿠폰 취소 요청 이벤트 발행
 */
@Slf4j
@org.springframework.core.annotation.Order(2)
@Component
@RequiredArgsConstructor
public class ApplyCouponStep implements SagaStep {

    private final SagaEventPublisher eventPublisher;

    @Override
    public String getName() {
        return "쿠폰적용";
    }

    @Override
    public void execute(OrderSaga saga, Order order) {
        UUID couponUserId = order.getCouponUserId();

        // 쿠폰이 없으면 스킵
        if (couponUserId == null) {
            log.info("[SAGA][STEP][{}][SKIP] sagaId={}, orderId={}, reason=쿠폰없음",
                    getName(), saga.getSagaId(), order.getOrderId());
            return;
        }

        log.info("[SAGA][STEP][{}][EXECUTE] sagaId={}, orderId={}, couponUserId={}",
                getName(), saga.getSagaId(), order.getOrderId(), couponUserId);

        // 쿠폰 사용 요청 이벤트 발행
        CouponUseEventReq event = CouponUseEventReq.of(
                saga.getSagaId(),
                order.getOrderId(),
                couponUserId,
                order.getOrderAmount().getCouponDiscountAmount()
        );

        try {
            // Kafka 발행: Kafka Producer가 메시지 전송
            eventPublisher.publishCouponUse(event);

            log.info("[SAGA][STEP][{}][EVENT_PUBLISHED] sagaId={}, correlationId={}",
                    getName(), saga.getSagaId(), event.getCorrelationId());
        } catch (Exception e) {
            // Kafka 전송 실패 (네트워크 오류 등)
            log.error("[SAGA][STEP][{}][PUBLISH_FAILED] sagaId={}, error={}",
                    getName(), saga.getSagaId(), event.getCorrelationId());
            throw e;
        }
    }

    @Override
    public void compensate(OrderSaga saga, Order order) {
        UUID couponUserId = order.getCouponUserId();

        if (couponUserId == null) {
            return;
        }

        log.warn("[SAGA][STEP][{}][COMPENSATE] sagaId={}, orderId={}, couponUserId={}",
                getName(), saga.getSagaId(), order.getOrderId(), couponUserId);

        // 쿠폰 취소 요청 이벤트 발행
        CouponCancelEventReq event = CouponCancelEventReq.of(
                saga.getSagaId(),
                order.getOrderId(),
                couponUserId
        );

        try {
            eventPublisher.publishCouponCancel(event);

            log.info("[SAGA][STEP][{}][COMPENSATE_PUBLISHED] sagaId={}, correlationId={}",
                    getName(), saga.getSagaId(), event.getCorrelationId());
        } catch (Exception e) {
            log.error("[SAGA][STEP][{}][COMPENSATE_FAILED] sagaId={}, error={}",
                    getName(), saga.getSagaId(), e.getMessage(), e);
        }
    }
}