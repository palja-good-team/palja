package com.palja.order_service.application.saga.steps;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.application.port.CouponClient;
import com.palja.order_service.application.saga.SagaStep;
import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.application.saga.model.OrderSagaStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@org.springframework.core.annotation.Order(2)
@Component
@RequiredArgsConstructor
public class ApplyCouponStep implements SagaStep {

    private final CouponClient couponClient;

    @Override public String name() { return "쿠폰사용"; }
    @Override public OrderSagaStep successStep() { return OrderSagaStep.COUPON_APPLIED; }

    @Override
    public void execute(Order order) {
        UUID orderId = order.getOrderId();
        UUID couponUserId = order.getCouponUserId();

        if (couponUserId == null) {
            log.info("[SAGA][{}][SUCCESS] orderId={}, action=skip, reason=쿠폰없음",
                    name(), orderId);
            return; // 쿠폰 없으면 성공 처리(다음 step 진행)
        }

        log.debug("[SAGA][{}][EXECUTE] orderId={}, couponUserId={}", name(), orderId, couponUserId);

        long start = System.nanoTime();
        try {
            couponClient.useCoupon(
                    couponUserId,
                    orderId,
                    order.getOrderAmount().getCouponDiscountAmount()
            );

            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.info("[SAGA][{}][SUCCESS] action=use, orderId={}, couponUserId={}, elapsedMs={}",
                    name(), orderId, couponUserId, elapsedMs);

        } catch (Exception e) {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.error("[SAGA][{}][ERROR] action=use, orderId={}, couponUserId={}, elapsedMs={}, msg={}",
                    name(), orderId, couponUserId, elapsedMs, e.getMessage(), e);
            throw new BusinessException(OrderErrorCode.COUPON_APPLICATION_FAILED);
        }
    }

    @Override
    public void compensate(Order order) {
        UUID orderId = order.getOrderId();
        UUID couponUserId = order.getCouponUserId();

        if (couponUserId == null) return;

        log.warn("[SAGA][{}][COMPENSATE-START] orderId={}, couponUserId={}",
                name(), orderId, couponUserId);

        long start = System.nanoTime();
        try {
            couponClient.cancelCoupon(couponUserId, orderId);

            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.info("[SAGA][{}][COMPENSATE-SUCCESS] action=cancel, orderId={}, couponUserId={}, elapsedMs={}",
                    name(), orderId, couponUserId, elapsedMs);

        } catch (Exception e) {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.error("[SAGA][{}][COMPENSATE-FAIL] action=cancel, orderId={}, couponUserId={}, elapsedMs={}, msg={}",
                    name(), orderId, couponUserId, elapsedMs, e.getMessage(), e);
            throw e; // Orchestrator가 Best Effort로 삼킴
        }
    }
}