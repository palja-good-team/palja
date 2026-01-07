package com.palja.order_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.application.saga.model.OrderSaga;
import com.palja.order_service.application.service.OrderSagaService;
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
     * - 정상: 신규 생성
     * - Unique 위반: 기존 Saga 반환(정상 동작)
     * - Unique 위반인데 조회 불가: 데이터 불일치(비정상)
     */
    @Override
    @Transactional
    public OrderSaga findOrCreateByOrderId(UUID orderId) {
        try {
            OrderSaga saved = orderSagaRepository.save(OrderSaga.create(orderId));
            log.debug("Saga 생성 (created): orderId={} sagaId={}", orderId, saved.getSagaId());
            return saved;

        } catch (DataIntegrityViolationException e) {
            // 정상(동시성): 이미 생성됨 → 조회해서 반환
            return orderSagaRepository.findByOrderId(orderId)
                    .map(existing -> {
                        log.debug("Saga 이미 존재 (already exists): orderId={} sagaId={}",
                                orderId, existing.getSagaId());
                        return existing;
                    })
                    .orElseThrow(() -> {
                        // 비정상: Unique 위반인데 row가 없다 → 정합성 깨짐
                        log.error("Saga 불일치 상태 감지 (inconsistent state): orderId={} reason=UNIQUE_VIOLATION_BUT_NOT_FOUND",
                                orderId, e);
                        return new BusinessException(OrderErrorCode.ORDER_SAGA_INCONSISTENT_STATE);
                    });
        }
    }

    /**
     * sagaId로 Saga 조회 (없으면 비즈니스 예외)
     */
    @Override
    public OrderSaga findBySagaId(UUID sagaId) {
        return orderSagaRepository.findBySagaId(sagaId)
                .orElseThrow(() -> {
                    log.error("Saga 조회 실패 (not found): sagaId={}", sagaId);
                    return new BusinessException(OrderErrorCode.ORDER_SAGA_NOT_FOUND);
                });
    }

    /**
     * Saga 저장
     */
    @Override
    @Transactional
    public void save(OrderSaga saga) {
        orderSagaRepository.save(saga);
    }
}