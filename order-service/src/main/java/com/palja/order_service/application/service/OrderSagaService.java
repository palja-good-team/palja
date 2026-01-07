package com.palja.order_service.application.service;

import com.palja.order_service.application.saga.model.OrderSaga;

import java.util.UUID;

public interface OrderSagaService {

    /**
     * Saga 저장
     */
    void save(OrderSaga saga);

    /**
     * Saga 생성 (멱등성 보장)
     *
     * - 정상: 신규 생성
     * - Unique 위반: 기존 Saga 반환(정상 동작)
     * - Unique 위반인데 조회 불가: 데이터 불일치(비정상)
     */
    OrderSaga findOrCreateByOrderId(UUID orderId);

    /**
     * sagaId로 Saga 조회 (없으면 비즈니스 예외)
     */
    OrderSaga findBySagaId(UUID sagaId);
}