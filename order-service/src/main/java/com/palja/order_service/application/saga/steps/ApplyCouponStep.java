package com.palja.order_service.application.saga.steps;

import com.palja.order_service.application.dto.event.request.CouponCancelEventReq;
import com.palja.order_service.application.dto.event.request.CouponUseEventReq;
import com.palja.order_service.application.port.kafka.OrderEventPublisher;
import com.palja.order_service.application.saga.SagaStep;
import com.palja.order_service.application.saga.model.OrderSaga;
import com.palja.order_service.application.saga.model.OrderSagaStep;
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

    private final OrderEventPublisher eventPublisher;

    @Override
    public String getName() {
        return "쿠폰적용";
    }

    @Override
    public OrderSagaStep getStepType() {
        return OrderSagaStep.COUPON_APPLIED;
    }

    @Override
    public void execute(OrderSaga saga, Order order) {
        UUID couponUserId = order.getCouponUserId();

        // 쿠폰이 없으면 스킵
        if (couponUserId == null) {
            log.info("[SAGA][ORDER][COUPON_USE][SKIP] sagaId={} orderId={} reason=NO_COUPON",
                    saga.getSagaId(), order.getOrderId());
            return;
        }

        log.info("[SAGA][ORDER][COUPON_USE][READY] sagaId={} orderId={} couponUserId={}",
                saga.getSagaId(), order.getOrderId(), couponUserId);

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

            // Producer에서 [KAFKA][ORDER][COUPON_USE][PUBLISHED] 찍고 있음
            // "사가 단계가 발행 요청을 완료했다" 정도만 남김
            log.info("[SAGA][ORDER][COUPON_USE][PUBLISHED] sagaId={} orderId={}",
                    saga.getSagaId(), order.getOrderId());

        } catch (Exception e) {
            // Kafka 전송 실패 (네트워크 오류 등)
            log.error("[SAGA][ORDER][COUPON_USE][FAILED] sagaId={} orderId={} reason={}",
                    saga.getSagaId(), order.getOrderId(), e.getClass().getSimpleName(), e);
            throw e;
        }
    }

    @Override
    public void compensate(OrderSaga saga, Order order) {
        UUID couponUserId = order.getCouponUserId();

        // 쿠폰이 없으면 보상도 없음
        if (couponUserId == null) {
            return;
        }

        log.warn("[SAGA][ORDER][COUPON_CANCEL][START] sagaId={} orderId={} couponUserId={}",
                saga.getSagaId(), order.getOrderId(), couponUserId);

        // 쿠폰 취소 요청 이벤트 발행
        CouponCancelEventReq event = CouponCancelEventReq.of(
                saga.getSagaId(),
                order.getOrderId(),
                couponUserId
        );

        try {
            eventPublisher.publishCouponCancel(event);

            log.info("[SAGA][ORDER][COUPON_CANCEL][PUBLISHED] sagaId={} orderId={}",
                    saga.getSagaId(), order.getOrderId());

        } catch (Exception e) {
            // 보상은 Best Effort: 실패해도 계속 진행
            log.error("[SAGA][ORDER][COUPON_CANCEL][FAILED] sagaId={} orderId={} reason={}",
                    saga.getSagaId(), order.getOrderId(), e.getClass().getSimpleName(), e);
        }
    }
}