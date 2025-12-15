package com.palja.order_service.application.saga;

import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.vo.OrderSagaStep;

/**
 * - 모든 Saga 단계가 따라야 함
 * - 각 단계는 반드시 "실행"과 "보상" 로직을 가져야 함
 * - Orchestrator가 모든 단계를 동일한 방식으로 다루기 위해 사용
 * - 새로운 단계 추가 시 일관된 구조 유지
 */
public interface SagaStep {

    /**
     * 【단계 이름】
     * - 로깅과 모니터링을 위한 이름
     */
    String name();

    OrderSagaStep successStep();

    /**
     * 【단계 실행】
     * - 이 단계에서 해야 할 작업 수행
     * - 예: 재고 차감, 쿠폰 사용, 결제 생성
     */
    void execute(Order order);

    /**
     * 【보상 트랜잭션】
     * - execute()에서 한 작업을 되돌림
     * - 예: 재고 복구, 쿠폰 복구, 결제 취소
     */
    void compensate(Order order);
}