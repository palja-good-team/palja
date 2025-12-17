package com.palja.order_service.application.service;

import com.palja.order_service.application.saga.model.OrderSaga;

import java.util.UUID;

public interface OrderSagaService {

    // Saga 저장
    void save(OrderSaga saga);

    /**
     * Saga 조회 또는 생성 (멱등성 보장)
     * - 이미 존재하면 기존 Saga 반환
     * - 없으면 새로 생성
     */
    OrderSaga findOrCreateByOrderId(UUID orderId);

    /**
     * Saga 조회 (존재하지 않으면 예외)
     */
    OrderSaga findByOrderId(UUID orderId);
    OrderSaga findBySagaId(UUID sagaId);

    /**
     * Saga 재조회 (최신 상태)
     * - 낙관적 락 충돌 후 재시도 시 사용
     */
    OrderSaga reloadByOrderId(UUID orderId);
    OrderSaga reload(UUID sagaId);

    /**
     * Saga 안전 재조회 (예외 없이 fallback)
     * - 실패 처리 중 조회 실패 시 새로 생성
     * - 거의 발생하지 않지만 안전장치용
     */
    OrderSaga safeReloadForFailByOrderId(UUID orderId);
    OrderSaga safeReloadForFail(UUID sagaId);
}