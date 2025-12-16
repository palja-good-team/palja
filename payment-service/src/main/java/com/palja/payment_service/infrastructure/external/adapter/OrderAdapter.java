package com.palja.payment_service.infrastructure.external.adapter;

import com.palja.common.exception.BusinessException;
import com.palja.common.response.ApiResponse;
import com.palja.payment_service.application.dto.external.OrderRes;
import com.palja.payment_service.application.port.OrderClient;
import com.palja.payment_service.exception.PaymentErrorCode;
import com.palja.payment_service.infrastructure.external.OrderFeignClient;
import com.palja.payment_service.infrastructure.external.dto.request.CompleteOrderPaymentDTO;
import com.palja.payment_service.infrastructure.external.dto.response.OrderDTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderAdapter implements OrderClient {

    private final OrderFeignClient orderFeignClient;

    @Override
    public OrderRes getOrderByOrderId(UUID orderId) {
        log.debug("주문 조회 요청: orderId={}", orderId);

        try {
            ApiResponse<OrderDTO> response = orderFeignClient.getOrderByOrderId(orderId);
            OrderDTO dto = response.data();

            if (dto == null) {
                log.error("주문 정보 없음(응답 body null): orderId={}", orderId);
                throw new BusinessException(PaymentErrorCode.ORDER_NOT_FOUND);
            }

            OrderRes orderRes = toOrderRes(dto);

            log.info("주문 조회 성공: orderId={}, userId={}, status={}",
                    orderRes.getOrderId(), orderRes.getUserId(), orderRes.getStatus());

            return orderRes;

        } catch (FeignException.NotFound e) {
            log.error("주문 정보 없음(404): orderId={}", orderId, e);
            throw new BusinessException(PaymentErrorCode.ORDER_NOT_FOUND);
        } catch (FeignException e) {
            log.error("주문 서비스 호출 실패: orderId={}, status={}, message={}",
                    orderId, e.status(), e.getMessage(), e);
            throw new BusinessException(PaymentErrorCode.ORDER_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("주문 조회 중 예상치 못한 오류: orderId={}, error={}",
                    orderId, e.getClass().getName(), e);
            throw new BusinessException(PaymentErrorCode.ORDER_SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public void completeOrderPayment(UUID orderId, UUID paymentId, BigDecimal paidAmount) {
        try {
            orderFeignClient.completeOrderPayment(
                    orderId,
                    CompleteOrderPaymentDTO.builder()
                            .paymentId(paymentId)
                            .paidAmount(paidAmount)
                            .build()
            );
            log.info("주문 결제완료 연동 성공: orderId={}, paymentId={}, paidAmount={}",
                    orderId, paymentId, paidAmount);
        } catch (FeignException e) {
            log.error("주문 결제완료 연동 실패: orderId={}, paymentId={}, status={}, msg={}",
                    orderId, paymentId, e.status(), e.getMessage(), e);
            throw new BusinessException(PaymentErrorCode.ORDER_SERVICE_UNAVAILABLE);
        }
    }

    private OrderRes toOrderRes(OrderDTO dto) {
        BigDecimal finalAmount = dto.getPricing() != null
                ? dto.getPricing().getFinalAmount()
                : null;

        log.info("OrderAdapter mapping: orderId={}, userId={}, status={}, finalAmount={}",
                dto.getOrderId(), dto.getUserId(), dto.getStatus(), finalAmount);

        return OrderRes.of(
                dto.getOrderId(),
                dto.getUserId(),
                dto.getStatus(),
                finalAmount
        );
    }
}
