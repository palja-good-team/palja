package com.palja.order_service.application.saga.steps;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.dto.external.PaymentCreateRes;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.application.port.PaymentClient;
import com.palja.order_service.application.saga.SagaStep;
import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.application.saga.model.OrderSagaStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@org.springframework.core.annotation.Order(3)
@Component
@RequiredArgsConstructor
public class CreatePaymentStep implements SagaStep {

    private final PaymentClient paymentClient;

    @Override public String name() { return "결제생성"; }
    @Override public OrderSagaStep successStep() { return OrderSagaStep.PAYMENT_CREATED; }

    @Override
    public void execute(Order order) {
        UUID orderId = order.getOrderId();

        log.debug("[SAGA][{}][EXECUTE] orderId={}, userId={}, finalAmount={}",
                name(), orderId, order.getUserId(), order.getOrderAmount().getFinalAmount());

        long start = System.nanoTime();

        try {
            PaymentCreateRes res = paymentClient.createPayment(
                    orderId,
                    order.getUserId(),
                    order.getOrderAmount().getFinalAmount(),
                    order.getStatus()
            );

            // Order에 paymentId 등록 (DB 저장은 Orchestrator가 함)
            order.registerPaymentId(res.getPaymentId());

            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.info("[SAGA][{}][SUCCESS] action=create, orderId={}, paymentId={}, elapsedMs={}",
                    name(), orderId, res.getPaymentId(), elapsedMs);

        } catch (Exception e) {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.error("[SAGA][{}][ERROR] action=create, orderId={}, elapsedMs={}, msg={}",
                    name(), orderId, elapsedMs, e.getMessage(), e);
            throw new BusinessException(OrderErrorCode.PAYMENT_CREATION_FAILED);
        }
    }

    @Override
    public void compensate(Order order) {
        UUID orderId = order.getOrderId();
        UUID paymentId = order.getPaymentId();

        // paymentId가 없으면 보상 불필요 (결제 생성 실패한 경우)
        if (paymentId == null) {
            log.debug("[SAGA][{}][COMPENSATE-SKIP] orderId={}, reason=결제ID없음", name(), orderId);
            return;
        }

        log.warn("[SAGA][{}][COMPENSATE-START] orderId={}, paymentId={}",
                name(), orderId, paymentId);

        long start = System.nanoTime();

        try {
            paymentClient.cancelPayment(
                    orderId,
                    paymentId,
                    order.getOrderAmount().getFinalAmount(),
                    "SAGA_ROLLBACK: 주문 생성 실패로 인한 결제 취소"
            );

            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.info("[SAGA][{}][COMPENSATE-SUCCESS] action=cancel, orderId={}, paymentId={}, elapsedMs={}",
                    name(), orderId, paymentId, elapsedMs);

        } catch (Exception e) {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.error("[SAGA][{}][COMPENSATE-FAIL] action=cancel, orderId={}, paymentId={}, elapsedMs={}, msg={}",
                    name(), orderId, paymentId, elapsedMs, e.getMessage(), e);
            throw e;
        }
    }
}