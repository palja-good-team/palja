package com.palja.order_service.application.service.listener;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.dto.event.OrderCreatedSagaEvent;
import com.palja.order_service.application.dto.external.PaymentCreateRes;
import com.palja.order_service.application.dto.event.OrderCreatedEvent;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.application.port.CouponClient;
import com.palja.order_service.application.port.PaymentClient;
import com.palja.order_service.application.port.ProductClient;
import com.palja.order_service.application.port.TimeDealClient;
import com.palja.order_service.application.saga.OrderCreationSagaOrchestrator;
import com.palja.order_service.application.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

/**
 * 주문 생성 이벤트 리스너
 * - 트랜잭션 커밋 후(AFTER_COMMIT) 외부 시스템 처리
 * - 재고 차감, 쿠폰 사용, 결제 생성
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreatedEventListener {

    private final ProductClient productClient;
    private final TimeDealClient timeDealClient;
    private final CouponClient couponClient;
    private final PaymentClient paymentClient;

    private final OrderService orderService;

    private final OrderCreationSagaOrchestrator sagaOrchestrator;

    //@Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreated(OrderCreatedSagaEvent event) {
        log.info("[EVENT][ORDER_CREATED] orderId={}", event.getOrderId());
        sagaOrchestrator.run(event.getOrderId());
    }

    /**
     * 주문 생성 완료 후 외부 시스템 처리
     * - 주문 생성 트랜잭션이 커밋된 후에 실행
     * - 주문 데이터가 DB에 확실히 저장된 상태
     * - 이 메서드에서 예외 발생 시 주문 생성 트랜잭션은 영향 받지 않음
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreatedV1(OrderCreatedEvent event) {
        log.info("[AFTER_COMMIT] 주문 생성 이벤트 처리 시작: orderId={}", event.getOrderId());

        try {
            reserveInventory(event);
            applyCoupon(event);
            UUID paymentId = createPayment(event);
            orderService.registerPaymentId(event.getOrderId(), paymentId);
            log.info("[AFTER_COMMIT] 주문 생성 이벤트 처리 완료: orderId={}", event.getOrderId());
        } catch (Exception e) {
            log.error("[AFTER_COMMIT] 주문 생성 이벤트 처리 실패: orderId={}", event.getOrderId(), e);
            // TODO: 실패 시 보상 트랜잭션 처리
        }
    }

    /**
     * 재고 차감
     * - 타임딜 주문: 타임딜 재고만 차감
     * - 일반 주문: 상품 재고만 차감
     */
    private void reserveInventory(OrderCreatedEvent event) {
        try {
            if (event.isTimeDealOrder()) {
                UUID timeDealId = event.getTimeDealId();
                timeDealClient.deductTimeDealStock(timeDealId, event.getQuantity());
                log.info("타임딜 재고 차감 완료: orderId={}, timeDealId={}, quantity={}",
                        event.getOrderId(), timeDealId, event.getQuantity());
            } else {
                productClient.deductProductStock(event.getProductId(), event.getQuantity());
                log.info("상품 재고 차감 완료: orderId={}, productId={}, quantity={}",
                        event.getOrderId(), event.getProductId(), event.getQuantity());
            }
        } catch (Exception e) {
            log.error("재고 차감 실패: orderId={}, productId={}, isTimeDeal={}",
                    event.getOrderId(), event.getProductId(), event.isTimeDealOrder(), e);
            // TODO: 보상 트랜잭션 처리
            throw new BusinessException(OrderErrorCode.INVENTORY_DEDUCTION_FAILED);
        }
    }

    // 쿠폰 사용
    private void applyCoupon(OrderCreatedEvent event) {
        UUID couponUserId = event.getCouponUserId();
        if (couponUserId == null) {
            return;
        }

        try {
            couponClient.useCoupon(couponUserId, event.getOrderId(), event.getCouponDiscountAmount());
            log.info("쿠폰 사용 완료: orderId={}, couponUserId={}", event.getOrderId(), couponUserId);
        } catch (Exception e) {
            log.error("쿠폰 사용 실패: orderId={}, couponUserId={}", event.getOrderId(), couponUserId, e);
            // TODO: 보상 트랜잭션 처리 (재고 복구)
            throw new BusinessException(OrderErrorCode.COUPON_APPLICATION_FAILED);
        }
    }

    /**
     * 결제 생성 (결제 완료 아님)
     * - 결제 엔티티만 생성 (paymentId는 결제 완료 시 저장)
     * - 주문 상태는 CREATED 유지
     */
    private UUID createPayment(OrderCreatedEvent event) {
        try {
            PaymentCreateRes payment = paymentClient.createPayment(
                    event.getOrderId(),
                    event.getUserId(),
                    event.getFinalAmount(),
                    event.getStatus()
            );

            log.info("결제 생성 완료: orderId={}, paymentId={}, amount={}",
                    event.getOrderId(), payment.getPaymentId(), payment.getAmount());

            return payment.getPaymentId();
        } catch (Exception e) {
            log.error("결제 생성 실패: orderId={}, amount={}",
                    event.getOrderId(), event.getFinalAmount(), e);
            // TODO: 보상 트랜잭션 처리 (재고 복구, 쿠폰 복구)
            throw new BusinessException(OrderErrorCode.PAYMENT_CREATION_FAILED);
        }
    }
}