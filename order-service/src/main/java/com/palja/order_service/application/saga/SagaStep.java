package com.palja.order_service.application.saga;

import com.palja.order_service.application.saga.model.OrderSaga;
import com.palja.order_service.domain.entity.Order;

/**
 * Saga Step 인터페이스
 * - 각 Step의 실행(forward) 및 보상(compensate) 정의
 * - 이벤트 발행만 수행하고 즉시 리턴
 */
public interface SagaStep {

    /**
     * Step 이름
     */
    String getName();

    /**
     * 정방향 실행 (이벤트 발행)
     */
    void execute(OrderSaga saga, Order order);

    /**
     * 보상 트랜잭션 (이벤트 발행)
     */
    void compensate(OrderSaga saga, Order order);
}