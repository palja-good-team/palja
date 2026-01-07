package com.palja.order_service.application.saga.steps;

import com.palja.order_service.application.event.dto.request.CouponCancelEventReq;
import com.palja.order_service.application.event.dto.request.CouponUseEventReq;
import com.palja.order_service.application.port.OrderEventPublisher;
import com.palja.order_service.application.saga.SagaStep;
import com.palja.order_service.application.saga.model.OrderSaga;
import com.palja.order_service.application.saga.model.OrderSagaStep;
import com.palja.order_service.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Step 2: 쿠폰 사용
 *
 * 정방향: 쿠폰 사용 요청 이벤트 발행
 * 보상: 쿠폰 취소 요청 이벤트 발행 (Best Effort)
 */
@Slf4j
@org.springframework.core.annotation.Order(2)
@Component
@RequiredArgsConstructor
public class UseCouponStep implements SagaStep {

    private final OrderEventPublisher eventPublisher;

    @Override
    public String getName() {
        return "쿠폰사용";
    }

    @Override
    public OrderSagaStep getStepType() {
        return OrderSagaStep.COUPON_USED;
    }

    @Override
    public void execute(OrderSaga saga, Order order) {
        UUID couponUserId = order.getCouponUserId();

        // 쿠폰 사용 요청 이벤트 발행
        CouponUseEventReq event = CouponUseEventReq.of(
                saga.getSagaId(),
                order.getOrderId(),
                couponUserId,
                order.getOrderAmount().getCouponDiscountAmount()
        );

        log.info("쿠폰 사용 요청 발행 (coupon use requested): sagaId={} orderId={} couponUserId={} discountAmount={}",
                saga.getSagaId(), order.getOrderId(), couponUserId, order.getOrderAmount().getCouponDiscountAmount());

        try {
            // Kafka 발행: Kafka Producer가 메시지 전송
            eventPublisher.publishCouponUse(event);
        } catch (Exception e) {
            // 발행 실패는 ERROR
            log.error("쿠폰 사용 요청 발행 실패 (coupon use publish failed): sagaId={} orderId={} couponUserId={} errorType={}",
                    saga.getSagaId(), order.getOrderId(), couponUserId, e.getClass().getSimpleName(), e);
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

        // 쿠폰 취소 요청 이벤트 발행
        CouponCancelEventReq event = CouponCancelEventReq.of(
                saga.getSagaId(),
                order.getOrderId(),
                couponUserId
        );

        log.warn("쿠폰 취소 보상 시작 (coupon cancel compensation started): sagaId={} orderId={} couponUserId={}",
                saga.getSagaId(), order.getOrderId(), couponUserId);

        try {
            // Kafka 발행
            eventPublisher.publishCouponCancel(event);

            log.info("쿠폰 취소 요청 발행 (coupon cancel requested): sagaId={} orderId={} couponUserId={}",
                    saga.getSagaId(), order.getOrderId(), couponUserId);

        } catch (Exception e) {
            // 보상 실패는 ERROR, Best Effort
            log.error("쿠폰 취소 요청 발행 실패 (coupon cancel publish failed): sagaId={} orderId={} couponUserId={} errorType={}",
                    saga.getSagaId(), order.getOrderId(), couponUserId, e.getClass().getSimpleName(), e);
        }
    }

    @Override
    public boolean isApplicable(OrderSaga saga, Order order) {
        return order.getCouponUserId() != null;
    }
}