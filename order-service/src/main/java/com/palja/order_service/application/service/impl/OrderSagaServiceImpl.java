package com.palja.order_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.application.service.OrderSagaService;
import com.palja.order_service.infrastructure.saga.model.OrderSaga;
import com.palja.order_service.domain.repository.OrderSagaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderSagaServiceImpl implements OrderSagaService {

    private final OrderSagaRepository orderSagaRepository;

    /**
     * Saga 생성 (멱등성 보장)
     *
     * [동작 방식]
     * 1. 새로운 Saga 생성 시도
     * 2. Unique 제약 위반 시 기존 Saga 조회
     * 3. 조회도 실패하면 예외 (데이터 불일치)
     */
    @Override
    @Transactional
    public OrderSaga findOrCreate(UUID orderId) {
        try {
            // 새로운 Saga 생성 시도
            OrderSaga saved = orderSagaRepository.save(OrderSaga.create(orderId));
            log.debug("[SAGA][CREATED] orderId={}, sagaId={}", orderId, saved.getSagaId());
            return saved;

        } catch (DataIntegrityViolationException e) {
            // Unique 제약 위반 → 이미 존재(정상 케이스)
            return orderSagaRepository.findByOrderId(orderId)
                    .map(saga -> {
                        log.debug("[SAGA][ALREADY_EXISTS] orderId={}, sagaId={}",
                                orderId, saga.getSagaId());
                        return saga;
                    })
                    .orElseThrow(() -> {
                        log.error("[SAGA][INCONSISTENT] orderId={}, unique violation but not found",
                                orderId, e);
                        return new BusinessException(OrderErrorCode.ORDER_SAGA_INCONSISTENT_STATE);
                    });
        }
    }

    /**
     * Saga 조회 (존재하지 않으면 예외)
     * - Saga가 반드시 존재해야 하는 경우
     */
    @Override
    public OrderSaga findByOrderId(UUID orderId) {
        return orderSagaRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.error("[SAGA][NOT_FOUND] orderId={}", orderId);
                    return new BusinessException(OrderErrorCode.ORDER_SAGA_NOT_FOUND);
                });
    }

    /**
     * Saga 재조회 (최신 상태)
     *
     * [사용 시점]
     * - 낙관적 락 충돌 후 재시도
     * - Step 실행 전 최신 상태 확인
     * - 동시 실행 감지
     *
     * [왜 필요한가?]
     * - JPA 영속성 컨텍스트는 같은 엔티티를 캐싱
     * - 다른 트랜잭션의 변경사항은 명시적 재조회 필요
     */
    @Override
    public OrderSaga reload(UUID orderId) {
        return orderSagaRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.error("[SAGA][RELOAD_FAILED] orderId={}, saga not found", orderId);
                    return new IllegalStateException(
                            "Saga가 초기화되지 않았습니다. orderId=" + orderId
                    );
                });
    }

    /**
     * Saga 안전 재조회 (예외 없이 fallback)
     *
     * [동작 방식]
     * 1. 재조회 시도
     * 2. 실패 시 새로운 Saga 생성 (fallback)
     *
     * [사용 시점]
     * - Saga 실패 처리 중 조회 실패 대비
     * - 거의 발생하지 않지만 안전장치
     *
     * [주의]
     * - 정상적인 상황에서는 발생하지 않음
     * - 만약 발생하면 데이터 불일치 가능성 있음
     */
    @Override
    @Transactional
    public OrderSaga safeReloadForFail(UUID orderId) {
        try {
            return reload(orderId);
        } catch (Exception e) {
            // 조회 실패 → fallback으로 새로 생성
            log.warn("[SAGA][SAFE_RELOAD] orderId={}, reload failed, creating fallback saga",
                    orderId, e);

            // Fallback: 새로 생성 (거의 발생 안 함)
            return OrderSaga.create(orderId);
        }
    }
}