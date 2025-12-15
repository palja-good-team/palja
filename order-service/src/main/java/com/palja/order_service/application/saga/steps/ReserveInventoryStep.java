package com.palja.order_service.application.saga.steps;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.application.port.ProductClient;
import com.palja.order_service.application.port.TimeDealClient;
import com.palja.order_service.application.saga.SagaStep;
import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.entity.OrderItem;
import com.palja.order_service.domain.vo.OrderSagaStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Step 1: 재고 차감
 * 정방향: 타임딜/일반 상품 재고 차감
 * 보상: 차감된 재고 복구 (Best Effort)
 */
@Slf4j
@org.springframework.core.annotation.Order(1)
@Component
@RequiredArgsConstructor
public class ReserveInventoryStep implements SagaStep {

    private final ProductClient productClient;
    private final TimeDealClient timeDealClient;

    @Override public String name() { return "재고차감"; }
    @Override public OrderSagaStep successStep() { return OrderSagaStep.INVENTORY_RESERVED; }

    // 재고 차감 실행
    @Override
    public void execute(Order order) {
        OrderItem item = order.requireOrderItem();
        UUID orderId = order.getOrderId();
        UUID productId = item.getProductId();
        Long quantity = item.getQuantity();
        boolean isTimeDeal = order.isTimeDealOrder();

        log.debug("[SAGA][{}][EXECUTE] orderId={}, productId={}, quantity={}, isTimeDeal={}",
                name(), orderId, productId, quantity, isTimeDeal);

        try {
            if (isTimeDeal) {
                deductTimeDealStock(orderId, item);
            } else {
                deductProductStock(orderId, productId, quantity);
            }
        } catch (BusinessException e) {
            log.error("[SAGA][{}][FAIL] orderId={}, productId={}, quantity={}, timeDeal={}, errorCode={}, msg={}",
                    name(), orderId, productId, quantity, isTimeDeal, e.getErrorCode(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("[SAGA][{}][ERROR] orderId={}, productId={}, quantity={}, timeDeal={}, msg={}",
                    name(), orderId, productId, quantity, isTimeDeal, e.getMessage(), e);
            throw new BusinessException(OrderErrorCode.INVENTORY_DEDUCTION_FAILED);
        }
    }

    // 타임딜 재고 차감
    private void deductTimeDealStock(UUID orderId, OrderItem item) {
        UUID timeDealId = item.getTimeDealId(); // 가능하면 requireTimeDealId()로
        Long quantity = item.getQuantity();

        long start = System.nanoTime();
        try {
            timeDealClient.deductTimeDealStock(timeDealId, quantity);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;

            log.info("[SAGA][{}][SUCCESS] action=deduct, target=timedeal, orderId={}, timeDealId={}, quantity={}, elapsedMs={}",
                    name(), orderId, timeDealId, quantity, elapsedMs);
        } catch (Exception e) {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.error("[SAGA][{}][FAIL] action=deduct, target=timedeal, orderId={}, timeDealId={}, quantity={}, elapsedMs={}, msg={}",
                    name(), orderId, timeDealId, quantity, elapsedMs, e.getMessage(), e);
            throw e;
        }
    }

    // 일반 상품 재고 차감
    private void deductProductStock(UUID orderId, UUID productId, Long quantity) {
        long start = System.nanoTime();
        try {
            productClient.deductProductStock(productId, quantity);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;

            log.info("[SAGA][{}][SUCCESS] action=deduct, target=product, orderId={}, productId={}, quantity={}, elapsedMs={}",
                    name(), orderId, productId, quantity, elapsedMs);
        } catch (Exception e) {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.error("[SAGA][{}][FAIL] action=deduct, target=product, orderId={}, productId={}, quantity={}, elapsedMs={}, msg={}",
                    name(), orderId, productId, quantity, elapsedMs, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 재고 복구 (보상 트랜잭션)
     * - 실패해도 예외를 던지지 않음
     * - 실패 시 로그만 남기고 계속 진행
     * - 수동 처리 또는 배치 작업으로 후처리
     */
    @Override
    public void compensate(Order order) {
        OrderItem item = order.requireOrderItem();
        UUID orderId = order.getOrderId();
        UUID productId = item.getProductId();
        Long quantity = item.getQuantity();
        boolean isTimeDeal = order.isTimeDealOrder();

        log.warn("[SAGA][{}][COMPENSATE-START] orderId={}, productId={}, quantity={}, timeDeal={}",
                name(), orderId, productId, quantity, isTimeDeal);

        try {
            if (isTimeDeal) {
                restoreTimeDealStock(orderId, item);
            } else {
                restoreProductStock(orderId, productId, quantity);
            }
        } catch (Exception e) {
            log.error("[SAGA][{}][COMPENSATE-FAIL] orderId={}, productId={}, quantity={}, timeDeal={}, msg={} - manual action required",
                    name(), orderId, productId, quantity, isTimeDeal, e.getMessage(), e);
        }
    }

    // 타임딜 재고 복구
    private void restoreTimeDealStock(UUID orderId, OrderItem item) {
        UUID timeDealId = item.getTimeDealId();
        Long quantity = item.getQuantity();

        long start = System.nanoTime();
        try {
            timeDealClient.restoreTimeDealStock(timeDealId, quantity);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;

            log.info("[SAGA][{}][COMPENSATE-SUCCESS] action=restore, target=timedeal, orderId={}, timeDealId={}, quantity={}, elapsedMs={}",
                    name(), orderId, timeDealId, quantity, elapsedMs);
        } catch (Exception e) {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.error("[SAGA][{}][COMPENSATE-FAIL] action=restore, target=timedeal, orderId={}, timeDealId={}, quantity={}, elapsedMs={}, msg={}",
                    name(), orderId, timeDealId, quantity, elapsedMs, e.getMessage(), e);
            throw e;
        }
    }

    // 일반 상품 재고 복구
    private void restoreProductStock(UUID orderId, UUID productId, Long quantity) {
        long start = System.nanoTime();
        try {
            productClient.restoreProductStock(productId, quantity);
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;

            log.info("[SAGA][{}][COMPENSATE-SUCCESS] action=restore, target=product, orderId={}, productId={}, quantity={}, elapsedMs={}",
                    name(), orderId, productId, quantity, elapsedMs);
        } catch (Exception e) {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.error("[SAGA][{}][COMPENSATE-FAIL] action=restore, target=product, orderId={}, productId={}, quantity={}, elapsedMs={}, msg={}",
                    name(), orderId, productId, quantity, elapsedMs, e.getMessage(), e);
            throw e;
        }
    }
}