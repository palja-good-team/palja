package com.palja.order_service.application.saga.config;

import com.palja.order_service.application.saga.SagaStep;
import com.palja.order_service.application.saga.steps.ApplyCouponStep;
import com.palja.order_service.application.saga.steps.CreatePaymentStep;
import com.palja.order_service.application.saga.steps.ReserveStockStep;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SagaConfiguration {

    @Bean
    public List<SagaStep> sagaSteps(
            ReserveStockStep reserveStockStep,
            ApplyCouponStep applyCouponStep,
            CreatePaymentStep createPaymentStep
    ) {
        return List.of(
                reserveStockStep,
                applyCouponStep,
                createPaymentStep
        );
    }
}