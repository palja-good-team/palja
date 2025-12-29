package com.palja.order_service.application.port;

import com.palja.order_service.application.event.dto.request.*;

/**
 * Saga 이벤트 발행 포트 (아웃바운드)
 * - Application Layer에서 정의
 * - Infrastructure Layer에서 구현 (Kafka Producer)
 */
public interface OrderEventPublisher {

    // 재고 차감 요청
    void publishStockDecrease(StockDecreaseEventReq event);

    // 재고 복구 요청 (보상)
    void publishStockRestore(StockRestoreEventReq event);

    // 쿠폰 사용 요청
    void publishCouponUse(CouponUseEventReq event);

    // 쿠폰 취소 요청 (보상)
    void publishCouponCancel(CouponCancelEventReq event);

    // 결제 생성 요청
    void publishPaymentCreate(PaymentCreateEventReq event);

    // 결제 취소 요청
    void publishPaymentCancel(PaymentCancelEventReq event);

    void publishOrderCanceled(OrderCanceledEventReq event);
}