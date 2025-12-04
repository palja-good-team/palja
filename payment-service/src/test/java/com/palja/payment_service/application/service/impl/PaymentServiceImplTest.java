package com.palja.payment_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.payment_service.application.command.CancelPaymentCommand;
import com.palja.payment_service.application.command.CreatePaymentCommand;
import com.palja.payment_service.application.command.FindPaymentListByConditionCommand;
import com.palja.payment_service.application.dto.response.PGPaymentRes;
import com.palja.payment_service.application.dto.response.PaymentDetailRes;
import com.palja.payment_service.application.service.PGPaymentService;
import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.entity.PaymentLog;
import com.palja.payment_service.domain.repository.PaymentLogRepository;
import com.palja.payment_service.domain.repository.PaymentRepository;
import com.palja.payment_service.domain.vo.PaymentMethod;
import com.palja.payment_service.domain.vo.PaymentStatus;
import com.palja.payment_service.exception.PaymentErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
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
                .paymentMethod("CARD")
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
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_FAILED);

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

    @Test
    @DisplayName("결제 상태가 APPROVED인 결제건 전체 금액 취소 성공")
    void cancelPayment_success() {
        Payment payment = Payment.create(
                UUID.randomUUID(),
                1L,
                new BigDecimal("10000"),
                "KRW",
                PaymentMethod.CARD,
                "paymentKey123"
        );
        payment.approve("paymentKey123");

        given(paymentRepository.findById(payment.getId()))
                .willReturn(Optional.of(payment));

        PGPaymentRes pgRes = PGPaymentRes.builder()
                .paymentKey("paymentKey123")
                .pgResponseCode("SUCCESS")
                .pgResponseMessage("취소 성공")
                .success(true)
                .approvedAmount(new BigDecimal("10000"))
                .build();

        given(pgPaymentService.cancelPayment(payment, new BigDecimal("10000"), "전체 환불"))
                .willReturn(pgRes);

        PaymentDetailRes result = paymentService.cancelPayment(
                CancelPaymentCommand.builder()
                        .paymentId(payment.getId())
                        .userId(1L)
                        .cancelAmount(new BigDecimal("10000"))
                        .cancelReason("전체 환불")
                        .build()
        );

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.CANCELED.name());

        then(pgPaymentService).should()
                .cancelPayment(payment, new BigDecimal("10000"), "전체 환불");
    }

    @Test
    @DisplayName("부분 환불 시 결제 취소 실패")
    void cancelPayment_partialRefundNotAllowed() {
        Payment payment = Payment.create(
                UUID.randomUUID(),
                1L,
                new BigDecimal("10000"),
                "KRW",
                PaymentMethod.CARD,
                "paymentKey123"
        );
        payment.approve("paymentKey123");

        given(paymentRepository.findById(payment.getId()))
                .willReturn(Optional.of(payment));

        given(pgPaymentService.cancelPayment(any(Payment.class), any(), any()))
                .willThrow(new BusinessException(PaymentErrorCode.PAYMENT_NOT_PARTIAL_REFUND));

        assertThatThrownBy(() -> paymentService.cancelPayment(
                CancelPaymentCommand.builder()
                        .paymentId(payment.getId())
                        .userId(1L)
                        .cancelAmount(new BigDecimal("2000"))
                        .cancelReason("부분 환불 요청")
                        .build()
        ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_NOT_PARTIAL_REFUND);
    }

    @Test
    @DisplayName("취소 금액이 결제 금액을 초과하면 결제 취소 실패")
    void cancelPayment_exceedAmount() {
        Payment payment = Payment.create(
                UUID.randomUUID(),
                1L,
                new BigDecimal("10000"),
                "KRW",
                PaymentMethod.CARD,
                "paymentKey123"
        );
        payment.approve("paymentKey123");

        given(paymentRepository.findById(payment.getId()))
                .willReturn(Optional.of(payment));

        given(pgPaymentService.cancelPayment(any(Payment.class), any(), any()))
                .willThrow(new BusinessException(PaymentErrorCode.PAYMENT_EXCEED_AMOUNT));

        assertThatThrownBy(() -> paymentService.cancelPayment(
                CancelPaymentCommand.builder()
                        .paymentId(payment.getId())
                        .userId(1L)
                        .cancelAmount(new BigDecimal("12000"))
                        .cancelReason("환불 요청")
                        .build()
        ))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_EXCEED_AMOUNT);
    }

    @Test
    @DisplayName("paymentId로 결제 단건 조회 성공")
    void getPayment_success() {
        Payment payment = Payment.create(
                UUID.randomUUID(),
                1L,
                new BigDecimal("10000"),
                "KRW",
                PaymentMethod.CARD,
                "paymentKey123"
        );
        payment.approve("paymentKey123");

        UUID paymentId = payment.getId();

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));

        PaymentDetailRes result = paymentService.getPayment(paymentId);

        assertThat(result).isNotNull();
        assertThat(result.getPaymentId()).isEqualTo(paymentId);
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.APPROVED.name());
        assertThat(result.getAmount()).isEqualTo(new BigDecimal("10000"));
    }

    @Test
    @DisplayName("존재하지 않는 paymentId로 인해 결제 단건 조회 실패")
    void getPayment_failure_paymentIdNotFound() {
        UUID paymentId = UUID.randomUUID();

        given(paymentRepository.findById(paymentId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPayment(paymentId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("검색 조건에 따른 결제 목록 조회 성공")
    void getPayments_success() {
        String status = "APPROVED";
        Long userId = 1L;
        UUID orderId = UUID.randomUUID();
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        PageRequest pageRequest = PageRequest.of(0, 10);

        Payment payment1 = Payment.create(orderId, userId, new BigDecimal("10000"), "KRW", PaymentMethod.CARD, "paymentKey123");
        payment1.approve("paymentKey123");

        Payment payment2 = Payment.create(orderId, userId, new BigDecimal("20000"), "KRW", PaymentMethod.CARD, "paymentKey456");
        payment2.approve("paymentKey456");

        Page<Payment> paymentPage = new PageImpl<>(List.of(payment1, payment2), pageRequest, 2);

        given(paymentRepository.findPayments(
                PaymentStatus.APPROVED,
                userId,
                orderId,
                startDate,
                endDate,
                pageRequest
        )).willReturn(paymentPage);

        var result = paymentService.searchPayments(
                new FindPaymentListByConditionCommand("APPROVED", userId, orderId, startDate, endDate),
                pageRequest
        );

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getPaymentId()).isEqualTo(payment1.getId());
        assertThat(result.getContent().get(1).getPaymentId()).isEqualTo(payment2.getId());
    }

    @Test
    @DisplayName("검색 조건에 따른 결제 목록 조회 - 결과 없어서 실패")
    void getPayments_emptyResult() {
        Long userId = 999L;
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Payment> emptyPage = new PageImpl<>(List.of(), pageRequest, 0);

        given(paymentRepository.findPayments(
                null,
                userId,
                null,
                null,
                null,
                pageRequest
        )).willReturn(emptyPage);

        var result = paymentService.searchPayments(
                new FindPaymentListByConditionCommand(null, userId, null, null, null),
                pageRequest
        );

        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("결제 status가 PENDING 상태가 아닌 결제 삭제 실패")
    void deletePayment_notPending() {
        Payment payment = Payment.create(
                UUID.randomUUID(),
                1L,
                new BigDecimal("10000"),
                "KRW",
                PaymentMethod.CARD,
                "paymentKey123"
        );
        payment.approve("paymentKey123");

        given(paymentRepository.findById(payment.getId()))
                .willReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.deletePayment(payment.getId()))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_CANNOT_BE_DELETED);

        then(paymentRepository).should(never()).deleteById(payment.getId());
    }
}
