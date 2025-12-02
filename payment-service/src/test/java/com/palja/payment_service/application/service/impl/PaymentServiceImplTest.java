package com.palja.payment_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.payment_service.application.command.CreatePaymentCommand;
import com.palja.payment_service.application.dto.response.PGPaymentRes;
import com.palja.payment_service.application.dto.response.PaymentDetailRes;
import com.palja.payment_service.application.service.PGPaymentService;
import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.entity.PaymentLog;
import com.palja.payment_service.domain.repository.PaymentLogRepository;
import com.palja.payment_service.domain.repository.PaymentRepository;
import com.palja.payment_service.domain.vo.PaymentMethod;
import com.palja.payment_service.domain.vo.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentLogRepository paymentLogRepository;

    @Mock
    private PGPaymentService pgPaymentService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private CreatePaymentCommand command;

    @BeforeEach
    void setUp() {
        command = CreatePaymentCommand.builder()
                .orderId(UUID.randomUUID())
                .userId(1L)
                .amount(new BigDecimal("10000"))
                .currency("KRW")
                .paymentMethod(PaymentMethod.CARD)
                .paymentKey("tviva20251202001935xysU8")
                .build();
    }

    @Test
    @DisplayName("PG 승인 성공 시 결제 상태가 APPROVED가 되고 결제 로그 생성")
    void createPayment_success() {
        PGPaymentRes pgRes = PGPaymentRes.builder()
                .paymentKey("pg-payment-key")
                .pgResponseCode("SUCCESS")
                .pgResponseMessage("성공")
                .success(true)
                .approvedAmount(command.amount())
                .build();

        given(paymentRepository.save(any(Payment.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        given(pgPaymentService.requestPayment(any(Payment.class)))
                .willReturn(pgRes);

        PaymentDetailRes result = paymentService.createPayment(command);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.APPROVED.name());
        assertThat(result.getPaymentKey()).isEqualTo("pg-payment-key");

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        then(paymentRepository).should(times(2)).save(paymentCaptor.capture());

        List<Payment> savedPayments = paymentCaptor.getAllValues();
        assertThat(savedPayments).hasSize(2);
        assertThat(savedPayments.get(1).getStatus()).isEqualTo(PaymentStatus.APPROVED);

        ArgumentCaptor<PaymentLog> logCaptor = ArgumentCaptor.forClass(PaymentLog.class);
        then(paymentLogRepository).should(times(2)).save(logCaptor.capture());

        List<PaymentLog> logs = logCaptor.getAllValues();
        assertThat(logs).hasSize(2);
        assertThat(logs.get(0).getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(logs.get(1).getStatus()).isEqualTo(PaymentStatus.APPROVED);
    }

    @Test
    @DisplayName("PG 응답 실패 시 Payment 상태는 FAILED가 되고 결제 로그 생성")
    void createPayment_pgFailure() {
        PGPaymentRes pgRes = PGPaymentRes.builder()
                .paymentKey("pg-payment-key")
                .pgResponseCode("ERROR")
                .pgResponseMessage("PG 에러")
                .success(false)
                .approvedAmount(null)
                .build();

        given(paymentRepository.save(any(Payment.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        given(pgPaymentService.requestPayment(any(Payment.class)))
                .willReturn(pgRes);

        assertThatThrownBy(() -> paymentService.createPayment(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", CommonErrorCode.FEIGN_ERROR);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        then(paymentRepository).should(times(2)).save(paymentCaptor.capture());

        List<Payment> savedPayments = paymentCaptor.getAllValues();
        assertThat(savedPayments).hasSize(2);
        assertThat(savedPayments.get(1).getStatus()).isEqualTo(PaymentStatus.FAILED);

        ArgumentCaptor<PaymentLog> logCaptor = ArgumentCaptor.forClass(PaymentLog.class);
        then(paymentLogRepository).should(times(2)).save(logCaptor.capture());

        List<PaymentLog> logs = logCaptor.getAllValues();
        assertThat(logs).hasSize(2);
        assertThat(logs.get(0).getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(logs.get(1).getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(logs.get(1).getPgResponseCode()).isEqualTo("ERROR");
    }

    /*
    TODO:  orderService에서 주문 시 주문금액과 결제 금액이 다를 때 결제 실패 Test Code(orderService Mock 설정 필요)
     */
}
