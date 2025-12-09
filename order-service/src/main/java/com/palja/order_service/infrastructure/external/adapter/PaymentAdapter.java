package com.palja.order_service.infrastructure.external.adapter;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.dto.PaymentMethod;
import com.palja.order_service.application.dto.response.PaymentCancelRes;
import com.palja.order_service.application.dto.response.PaymentCreateRes;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.application.service.PaymentService;
import com.palja.order_service.infrastructure.external.PaymentClient;
import com.palja.order_service.infrastructure.external.dto.request.CancelPaymentDTO;
import com.palja.order_service.infrastructure.external.dto.request.CreatePaymentDTO;
import com.palja.order_service.infrastructure.external.dto.response.PaymentCancelDTO;
import com.palja.order_service.infrastructure.external.dto.response.PaymentCreateDTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentAdapter implements PaymentService {

    private final PaymentClient paymentClient;

    @Override
    public PaymentCreateRes createPayment(UUID orderId, Long userId, BigDecimal amount, String paymentKey, PaymentMethod paymentMethod) {
        log.debug("결제 생성 요청: orderId={}, userId={}, amount={}, paymentMethod={}",
                orderId, userId, amount, paymentMethod);
        try {
            CreatePaymentDTO request = new CreatePaymentDTO(orderId, amount, paymentMethod, "KRW", paymentKey);
            PaymentCreateDTO response = paymentClient.createPayment(request).data();
            log.info("결제 생성 성공: paymentId={}", response.getPaymentId());
            return response.toResponse();
        } catch (FeignException e) {
            log.error("결제 API 호출 실패: orderId={}, status={}, message={}",
                    orderId, e.status(), e.getMessage(), e);
            throw new BusinessException(OrderErrorCode.PAYMENT_SERVICE_ERROR);
        } catch (Exception e) {
            log.error("결제 생성 중 예상치 못한 오류: orderId={}, error={}",
                    orderId, e.getClass().getName(), e);
            throw new BusinessException(OrderErrorCode.PAYMENT_FAILED);
        }
    }

    public PaymentCancelRes cancelPayment(UUID orderId, UUID paymentId, BigDecimal cancelAmount, String cancelReason) {
        log.info("결제 취소 요청 시작: orderId={}, paymentId={}", orderId, paymentId);
        try {
            CancelPaymentDTO request = new CancelPaymentDTO(cancelAmount, cancelReason);
            PaymentCancelDTO response = paymentClient.cancelPayment(paymentId, request).data();
            log.info("결제 생성 성공: paymentId={}", response.getPaymentId());
            return response.toResponse();
        } catch (FeignException e) {
            log.error("결제 취소 API 호출 실패: paymentId={}, status={}, message={}",
                    paymentId, e.status(), e.getMessage(), e);
            throw new BusinessException(OrderErrorCode.PAYMENT_SERVICE_ERROR);
        } catch (Exception e) {
            log.error("결제 취소 중 예상치 못한 오류: paymentId={}, error={}",
                    paymentId, e.getClass().getName(), e);
            throw new BusinessException(OrderErrorCode.PAYMENT_FAILED);
        }
    }
}