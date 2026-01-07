package com.palja.order_service.application.saga.config;

import com.palja.order_service.application.saga.SagaStep;
import com.palja.order_service.application.saga.model.OrderSagaStep;
import com.palja.order_service.application.saga.steps.CreatePaymentStep;
import com.palja.order_service.application.saga.steps.DecreaseStockStep;
import com.palja.order_service.application.saga.steps.UseCouponStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Saga Step Map Configuration
 * - Step Enum ↔ Step 구현체 매핑
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SagaConfig {

    private final DecreaseStockStep decreaseStockStep;
    private final UseCouponStep useCouponStep;
    private final CreatePaymentStep createPaymentStep;

    @Bean
    public Map<OrderSagaStep, SagaStep> stepMap() {
        EnumMap<OrderSagaStep, SagaStep> map = new EnumMap<>(OrderSagaStep.class);
        map.put(OrderSagaStep.STOCK_DECREASED, decreaseStockStep);
        map.put(OrderSagaStep.COUPON_USED, useCouponStep);
        map.put(OrderSagaStep.PAYMENT_CREATED, createPaymentStep);

        validate(map);

        Map<OrderSagaStep, SagaStep> immutable = Map.copyOf(map);

        log.info("Saga Step 매핑 로딩 완료: stepCount={} steps={}", immutable.size(), immutable.keySet());

        return immutable;
    }

    private static void validate(EnumMap<OrderSagaStep, SagaStep> map) {
        // Key ↔ StepType 불일치/NULL 방지
        map.forEach((key, step) -> {
            if (step == null) {
                throw new IllegalStateException("Saga Step 매핑 오류: null mapping. step=" + key);
            }
            OrderSagaStep actual = step.getStepType();
            if (actual != key) {
                throw new IllegalStateException(String.format(
                        "Saga Step 매핑 불일치: mapKey=%s, stepType=%s, stepClass=%s",
                        key, actual, step.getClass().getSimpleName()
                ));
            }
        });

        // 실행 대상 Step 누락 방지
        List<OrderSagaStep> required = OrderSagaStep.getExecutableSteps();
        for (OrderSagaStep step : required) {
            if (!map.containsKey(step)) {
                throw new IllegalStateException("Saga Step 매핑 누락: step=" + step);
            }
        }
    }
}