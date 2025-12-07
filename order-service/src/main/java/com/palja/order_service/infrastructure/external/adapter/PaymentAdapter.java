package com.palja.order_service.infrastructure.external.adapter;

import com.palja.order_service.application.dto.PaymentCancelRes;
import com.palja.order_service.application.dto.PaymentRes;
import com.palja.order_service.application.service.PaymentService;
import com.palja.order_service.infrastructure.external.dto.response.PaymentCancelDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentAdapter implements PaymentService {

    // TODO: 결제 서비스 연동 시 PaymentClient 주입 및 구현 추가
    //private final PaymentClient paymentClient;

    @Override
    public PaymentRes createPayment(UUID orderId, Long userId, BigDecimal amount, String paymentMethod) {
        log.debug("결제 생성 요청: orderId={}, userId={}, amount={}, paymentMethod={}",
                orderId, userId, amount, paymentMethod);

        // TODO: payment-service 연동 시 FeignClient 호출 사용
        // CreatePaymentDTO request = new CreatePaymentDTO(orderId, userId, amount, paymentMethod);
        // PaymentDTO response = paymentClient.createPayment(request).data();
        // TODO: 실제 결제 서비스 연동 시 위의 코드로 교체
        // 임시 더미 데이터
        PaymentDTO dummy = PaymentDTO.dummy(orderId, userId, amount);

        return PaymentRes.of(dummy.getPaymentId(), dummy.getAmount());
    }

    public PaymentCancelRes cancelPayment(UUID orderId, UUID paymentId) {
        log.info("결제 취소 요청 시작: paymentId={}", paymentId);
        // TODO: payment-service 연동 시 FeignClient 호출 사용
        //CancelPaymentDTO request = new CancelPaymentDTO("주문 취소");
        //PaymentCancelDTO response = paymentClient.cancelPayment(paymentId, request).data();
        // TODO: 실제 결제 서비스 연동 시 위의 코드로 교체
        PaymentCancelDTO dummy = PaymentCancelDTO.dummy(orderId, paymentId);

        return PaymentCancelRes.of(dummy.getPaymentId(), dummy.getAmount(), dummy.getStatus());
    }
}