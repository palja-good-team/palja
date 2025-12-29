package com.palja.order_service.application.saga.config;

import com.palja.order_service.application.saga.SagaStep;
import com.palja.order_service.application.saga.model.OrderSagaStep;
import com.palja.order_service.application.saga.steps.ApplyCouponStep;
import com.palja.order_service.application.saga.steps.CreatePaymentStep;
import com.palja.order_service.application.saga.steps.DecreaseStockStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Saga Step Map Configuration
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SagaConfiguration {

    private final DecreaseStockStep decreaseStockStep;
    private final ApplyCouponStep applyCouponStep;
    private final CreatePaymentStep createPaymentStep;

    @Bean
    public Map<OrderSagaStep, SagaStep> stepMap() {
        Map<OrderSagaStep, SagaStep> map = Map.of(
                OrderSagaStep.STOCK_DECREASED, decreaseStockStep,
                OrderSagaStep.COUPON_APPLIED, applyCouponStep,
                OrderSagaStep.PAYMENT_CREATED, createPaymentStep
        );

        // 시작 시 검증
        map.forEach((stepEnum, step) -> {
            if (step.getStepType() != stepEnum) {
                throw new IllegalStateException(
                        String.format("Step 구현 불일치: Map Key=%s, Step.getStepType()=%s",
                                stepEnum, step.getStepType()));
            }
        });

        log.info("[SAGA][CONFIG] Step Map 초기화 완료: {} steps", map.size());

        return map;
    }
}