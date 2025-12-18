package com.palja.payment_service.application.service.impl;

import com.palja.common.auditor.AuditorContext;
import com.palja.common.exception.BusinessException;
import com.palja.common.vo.UserRole;
import com.palja.payment_service.application.command.CancelPaymentCommand;
import com.palja.payment_service.application.command.CompletePaymentCommand;
import com.palja.payment_service.application.command.CreatePaymentCommand;
import com.palja.payment_service.application.command.FindPaymentListByConditionCommand;
import com.palja.payment_service.application.dto.external.OrderRes;
import com.palja.payment_service.application.dto.external.UserRes;
import com.palja.payment_service.application.dto.response.CancelPaymentRes;
import com.palja.payment_service.application.dto.response.CreatePaymentRes;
import com.palja.payment_service.application.dto.response.PGPaymentRes;
import com.palja.payment_service.application.dto.response.ReadPaymentDetailRes;
import com.palja.payment_service.application.dto.response.ReadPaymentSummaryRes;
import com.palja.payment_service.application.port.OrderClient;
import com.palja.payment_service.application.port.UserClient;
import com.palja.payment_service.application.service.PGPaymentService;
import com.palja.payment_service.application.validator.PaymentValidator;
import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.entity.PaymentLog;
import com.palja.payment_service.domain.repository.PaymentLogRepository;
import com.palja.payment_service.domain.repository.PaymentRepository;
import com.palja.payment_service.domain.vo.PaymentMethod;
import com.palja.payment_service.domain.vo.PaymentStatus;
import com.palja.payment_service.exception.PaymentErrorCode;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.*;
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

    @Mock
    private PaymentValidator paymentValidator;

    @Mock
    private OrderClient orderClient;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @AfterEach
    void tearDown() {
        AuditorContext.clear();
    }

    @Test
    @DisplayName("PG 승인 성공 시 결제 상태가 PENDING이고 결제 로그 생성")
    void createPayment_success() {
        UUID orderId = UUID.randomUUID();

        AuditorContext.set("testUser", UserRole.CUSTOMER);

        CreatePaymentCommand command = CreatePaymentCommand.builder()
                .orderId(orderId)
                .userId(1L)
                .loginId("testUser")
                .amount(new BigDecimal("10000"))
                .orderStatus("CREATED")
                .build();

        OrderRes orderRes = OrderRes.of(orderId, 1L, "CREATED", new BigDecimal("10000"));
        UserRes userRes = UserRes.of(1L, "testUser", "testUser", "test@example.com", UserRole.CUSTOMER, "ACTIVE");

        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.empty());
        given(orderClient.getOrderByOrderId(orderId)).willReturn(orderRes);
        given(userClient.getUserByLoginId("testUser")).willReturn(userRes);

        UUID savedPaymentId = UUID.randomUUID();
        given(paymentRepository.save(any(Payment.class)))
                .willAnswer(invocation -> {
                    Payment p = invocation.getArgument(0);
                    ReflectionTestUtils.setField(p, "id", savedPaymentId);
                    return p;
                });
        given(paymentLogRepository.save(any(PaymentLog.class))).willAnswer(invocation -> invocation.getArgument(0));

        CreatePaymentRes result = paymentService.createPayment(command);

        assertThat(result).isNotNull();
        assertThat(result.getPaymentId()).isEqualTo(savedPaymentId);
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("10000"));
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PENDING.name());

        then(paymentValidator).should().validateCreatePayment(eq(command), eq(orderRes), eq(userRes));

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        then(paymentRepository).should(times(1)).save(paymentCaptor.capture());
        assertThat(paymentCaptor.getValue().getStatus()).isEqualTo(PaymentStatus.PENDING);

        ArgumentCaptor<PaymentLog> logCaptor = ArgumentCaptor.forClass(PaymentLog.class);
        then(paymentLogRepository).should(times(1)).save(logCaptor.capture());
        assertThat(logCaptor.getValue().getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(logCaptor.getValue().getPgResponseCode()).isEqualTo("PENDING");

        then(pgPaymentService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("이미 결제가 존재하면 기존 결제 정보를 반환하고 생성 로직을 스킵")
    void createPayment_success_existingPayment() {
        UUID orderId = UUID.randomUUID();

        AuditorContext.set("testUser", UserRole.CUSTOMER);

        CreatePaymentCommand command = CreatePaymentCommand.builder()
                .orderId(orderId)
                .userId(1L)
                .loginId("testUser")
                .amount(new BigDecimal("10000"))
                .orderStatus("CREATED")
                .build();

        Payment existing = Payment.createPending(orderId, 1L, new BigDecimal("10000"));
        UUID existingId = UUID.randomUUID();
        ReflectionTestUtils.setField(existing, "id", existingId);

        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.of(existing));

        CreatePaymentRes result = paymentService.createPayment(command);

        assertThat(result.getPaymentId()).isEqualTo(existingId);
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.PENDING.name());

        then(orderClient).shouldHaveNoInteractions();
        then(userClient).shouldHaveNoInteractions();
        then(paymentValidator).shouldHaveNoInteractions();
        then(paymentRepository).should(never()).save(any());
        then(paymentLogRepository).shouldHaveNoInteractions();
        then(pgPaymentService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("주문 userId와 결제 요청 userId가 다르면 결제 생성 실패")
    void createPayment_failure_userIdMismatch() {
        UUID orderId = UUID.randomUUID();

        AuditorContext.set("testUser", UserRole.CUSTOMER);

        CreatePaymentCommand command = CreatePaymentCommand.builder()
                .orderId(orderId)
                .userId(2L)
                .loginId("testUser")
                .amount(new BigDecimal("10000"))
                .orderStatus("CREATED")
                .build();

        OrderRes orderRes = OrderRes.of(orderId, 1L, "CREATED", new BigDecimal("10000"));
        UserRes userRes = UserRes.of(1L, "testUser", "testUser", "test@example.com", UserRole.CUSTOMER, "ACTIVE");

        given(paymentRepository.findByOrderId(orderId)).willReturn(Optional.empty());
        given(orderClient.getOrderByOrderId(orderId)).willReturn(orderRes);
        given(userClient.getUserByLoginId("testUser")).willReturn(userRes);

        assertThatThrownBy(() -> paymentService.createPayment(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.INVALID_PAYMENT_INFO);

        then(paymentValidator).shouldHaveNoInteractions();
        then(paymentRepository).should(never()).save(any());
        then(paymentLogRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("결제 완료 성공 시 APPROVED로 변경되고 결제 로그 생성 및 주문 결제완료 연동")
    void completePayment_success() {
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        AuditorContext.set("testUser", UserRole.CUSTOMER);

        Payment payment = Payment.createPending(orderId, 1L, new BigDecimal("10000"));
        ReflectionTestUtils.setField(payment, "id", paymentId);

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));

        PGPaymentRes pgRes = PGPaymentRes.builder()
                .paymentKey("pg-payment-key")
                .pgResponseCode("SUCCESS")
                .pgResponseMessage("토스 결제 성공")
                .success(true)
                .approvedAmount(new BigDecimal("10000"))
                .build();

        given(pgPaymentService.requestPayment(any(Payment.class))).willReturn(pgRes);
        given(paymentRepository.save(any(Payment.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(paymentLogRepository.save(any(PaymentLog.class))).willAnswer(invocation -> invocation.getArgument(0));

        CompletePaymentCommand command = CompletePaymentCommand.builder()
                .paymentId(paymentId)
                .paymentKey("pgKey")
                .loginId("testUser")
                .build();

        CreatePaymentRes result = paymentService.completePayment(command);

        assertThat(result).isNotNull();
        assertThat(result.getPaymentId()).isEqualTo(paymentId);
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.APPROVED.name());

        then(orderClient).should(times(1)).completeOrderPayment(eq(orderId), eq(paymentId), eq(new BigDecimal("10000")));

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        then(paymentRepository).should(times(1)).save(paymentCaptor.capture());
        assertThat(paymentCaptor.getValue().getStatus()).isEqualTo(PaymentStatus.APPROVED);

        ArgumentCaptor<PaymentLog> logCaptor = ArgumentCaptor.forClass(PaymentLog.class);
        then(paymentLogRepository).should(times(1)).save(logCaptor.capture());
        assertThat(logCaptor.getValue().getStatus()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(logCaptor.getValue().getPgResponseCode()).isEqualTo("SUCCESS");
    }

    @Test
    @DisplayName("존재하지 않는 paymentId로 결제 완료 실패")
    void completePayment_failure_paymentNotFound() {
        UUID paymentId = UUID.randomUUID();

        AuditorContext.set("testUser", UserRole.CUSTOMER);

        given(paymentRepository.findById(paymentId)).willReturn(Optional.empty());

        CompletePaymentCommand command = CompletePaymentCommand.builder()
                .paymentId(paymentId)
                .paymentKey("pgKey")
                .loginId("testUser")
                .build();

        assertThatThrownBy(() -> paymentService.completePayment(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_NOT_FOUND);

        then(pgPaymentService).shouldHaveNoInteractions();
        then(paymentRepository).should(never()).save(any());
        then(paymentLogRepository).shouldHaveNoInteractions();
        then(orderClient).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("이미 처리된 결제는 결제 완료 처리 실패")
    void completePayment_failure_alreadyProcessed() {
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        AuditorContext.set("testUser", UserRole.CUSTOMER);

        Payment payment = Payment.createPending(orderId, 1L, new BigDecimal("10000"));
        ReflectionTestUtils.setField(payment, "id", paymentId);
        payment.approve("alreadyKey");

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));

        CompletePaymentCommand command = CompletePaymentCommand.builder()
                .paymentId(paymentId)
                .paymentKey("pgKey")
                .loginId("testUser")
                .build();

        assertThatThrownBy(() -> paymentService.completePayment(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_ALREADY_PROCESSED);

        then(pgPaymentService).shouldHaveNoInteractions();
        then(paymentRepository).should(never()).save(any());
        then(paymentLogRepository).shouldHaveNoInteractions();
        then(orderClient).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("PG 응답 실패 시 결제 상태는 FAILED가 되고 결제 로그 생성")
    void completePayment_pgFailure() {
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        AuditorContext.set("testUser", UserRole.CUSTOMER);

        Payment payment = Payment.createPending(orderId, 1L, new BigDecimal("10000"));
        ReflectionTestUtils.setField(payment, "id", paymentId);

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));

        PGPaymentRes pgRes = PGPaymentRes.builder()
                .paymentKey("pgKey")
                .pgResponseCode("ERROR")
                .pgResponseMessage("PG 에러")
                .success(false)
                .approvedAmount(new BigDecimal("10000"))
                .build();

        given(pgPaymentService.requestPayment(any(Payment.class))).willReturn(pgRes);
        given(paymentRepository.save(any(Payment.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(paymentLogRepository.save(any(PaymentLog.class))).willAnswer(invocation -> invocation.getArgument(0));

        CompletePaymentCommand command = CompletePaymentCommand.builder()
                .paymentId(paymentId)
                .paymentKey("pgKey")
                .loginId("testUser")
                .build();

        assertThatThrownBy(() -> paymentService.completePayment(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_FAILED);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        then(paymentRepository).should(times(1)).save(paymentCaptor.capture());
        assertThat(paymentCaptor.getValue().getStatus()).isEqualTo(PaymentStatus.FAILED);

        ArgumentCaptor<PaymentLog> logCaptor = ArgumentCaptor.forClass(PaymentLog.class);
        then(paymentLogRepository).should(times(1)).save(logCaptor.capture());
        assertThat(logCaptor.getValue().getStatus()).isEqualTo(PaymentStatus.FAILED);
        assertThat(logCaptor.getValue().getPgResponseCode()).isEqualTo("ERROR");

        then(orderClient).should(never()).completeOrderPayment(any(), any(), any());
    }

    @Test
    @DisplayName("결제 상태가 APPROVED인 결제건 전체 금액 취소 성공")
    void cancelPayment_success() {
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        AuditorContext.set("testUser", UserRole.CUSTOMER);

        Payment payment = Payment.createPending(orderId, 1L, new BigDecimal("10000"));
        ReflectionTestUtils.setField(payment, "id", paymentId);
        payment.approve("paymentKey123");

        UserRes userRes = UserRes.of(1L, "testUser", "testUser", "test@example.com", UserRole.CUSTOMER, "ACTIVE");

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
        given(userClient.getUserByLoginId("testUser")).willReturn(userRes);

        PGPaymentRes pgRes = PGPaymentRes.builder()
                .paymentKey("paymentKey123")
                .pgResponseCode("SUCCESS")
                .pgResponseMessage("취소 성공")
                .success(true)
                .approvedAmount(new BigDecimal("10000"))
                .build();

        given(pgPaymentService.cancelPayment(eq(payment), eq(new BigDecimal("10000")), eq("전체 환불"))).willReturn(pgRes);
        given(paymentRepository.save(any(Payment.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(paymentLogRepository.save(any(PaymentLog.class))).willAnswer(invocation -> invocation.getArgument(0));

        CancelPaymentCommand cancelCommand = CancelPaymentCommand.builder()
                .paymentId(paymentId)
                .loginId("testUser")
                .cancelAmount(new BigDecimal("10000"))
                .cancelReason("전체 환불")
                .build();

        CancelPaymentRes result = paymentService.cancelPayment(cancelCommand);

        assertThat(result).isNotNull();
        assertThat(result.getPaymentId()).isEqualTo(paymentId);
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.CANCELED.name());
        assertThat(result.getCancelReason()).isEqualTo("전체 환불");

        then(paymentValidator).should().validateCancelPayment(eq(payment), eq(cancelCommand), eq(userRes));
        then(pgPaymentService).should().cancelPayment(eq(payment), eq(new BigDecimal("10000")), eq("전체 환불"));

        then(paymentRepository).should(times(1)).save(any(Payment.class));

        ArgumentCaptor<PaymentLog> logCaptor = ArgumentCaptor.forClass(PaymentLog.class);
        then(paymentLogRepository).should(times(1)).save(logCaptor.capture());
        assertThat(logCaptor.getValue().getStatus()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(logCaptor.getValue().getPgResponseMessage()).startsWith("[CANCEL_FAILED]");
    }

    @Test
    @DisplayName("존재하지 않는 paymentId로 인해 결제 취소 실패")
    void cancelPayment_failure_paymentNotFound() {
        UUID paymentId = UUID.randomUUID();

        AuditorContext.set("testUser", UserRole.CUSTOMER);

        given(paymentRepository.findById(paymentId)).willReturn(Optional.empty());

        CancelPaymentCommand cancelCommand = CancelPaymentCommand.builder()
                .paymentId(paymentId)
                .loginId("testUser")
                .cancelAmount(new BigDecimal("10000"))
                .cancelReason("전체 환불")
                .build();

        assertThatThrownBy(() -> paymentService.cancelPayment(cancelCommand))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_NOT_FOUND);

        then(userClient).shouldHaveNoInteractions();
        then(paymentValidator).shouldHaveNoInteractions();
        then(pgPaymentService).shouldHaveNoInteractions();
        then(paymentLogRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("PG 취소 실패 시 결제 취소 실패")
    void cancelPayment_failure_pgFailure() {
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        AuditorContext.set("testUser", UserRole.CUSTOMER);

        Payment payment = Payment.createPending(orderId, 1L, new BigDecimal("10000"));
        ReflectionTestUtils.setField(payment, "id", paymentId);
        payment.approve("paymentKey123");

        UserRes userRes = UserRes.of(1L, "testUser", "testUser", "test@example.com", UserRole.CUSTOMER, "ACTIVE");

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
        given(userClient.getUserByLoginId("testUser")).willReturn(userRes);

        PGPaymentRes pgRes = PGPaymentRes.builder()
                .paymentKey("paymentKey123")
                .pgResponseCode("ERROR")
                .pgResponseMessage("취소 실패")
                .success(false)
                .approvedAmount(null)
                .build();

        given(pgPaymentService.cancelPayment(any(Payment.class), any(BigDecimal.class), anyString()))
                .willReturn(pgRes);

        given(paymentLogRepository.save(any(PaymentLog.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        CancelPaymentCommand cancelCommand = CancelPaymentCommand.builder()
                .paymentId(paymentId)
                .loginId("testUser")
                .cancelAmount(new BigDecimal("10000"))
                .cancelReason("환불 요청")
                .build();

        assertThatThrownBy(() -> paymentService.cancelPayment(cancelCommand))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_CANCEL_FAILED);

        then(paymentValidator).should().validateCancelPayment(eq(payment), eq(cancelCommand), eq(userRes));
        then(paymentRepository).should(never()).save(any(Payment.class));

        ArgumentCaptor<PaymentLog> logCaptor = ArgumentCaptor.forClass(PaymentLog.class);
        then(paymentLogRepository).should(times(1)).save(logCaptor.capture());
        assertThat(logCaptor.getValue().getStatus()).isEqualTo(PaymentStatus.APPROVED);
        assertThat(logCaptor.getValue().getPgResponseMessage()).startsWith("[CANCEL_FAILED]");

        then(pgPaymentService).should(times(1))
                .cancelPayment(any(Payment.class), any(BigDecimal.class), anyString());
    }

    @Test
    @DisplayName("paymentId로 결제 단건 조회 성공")
    void getPayment_success() {
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        AuditorContext.set("testUser", UserRole.CUSTOMER);

        Payment payment = Payment.createPending(orderId, 1L, new BigDecimal("10000"));
        ReflectionTestUtils.setField(payment, "id", paymentId);
        payment.approve("paymentKey123");

        UserRes userRes = UserRes.of(1L, "testUser", "testUser", "test@example.com", UserRole.CUSTOMER, "ACTIVE");

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
        given(userClient.getUserByLoginId("testUser")).willReturn(userRes);

        ReadPaymentDetailRes result = paymentService.getPayment(paymentId);

        assertThat(result).isNotNull();
        assertThat(result.getPaymentId()).isEqualTo(paymentId);
        assertThat(result.getOrderId()).isEqualTo(orderId);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(PaymentStatus.APPROVED.name());

        then(paymentValidator).should().validateGetPayment(eq(payment), eq(userRes));
    }

    @Test
    @DisplayName("존재하지 않는 paymentId로 인해 결제 단건 조회 실패")
    void getPayment_failure_paymentIdNotFound() {
        UUID paymentId = UUID.randomUUID();

        AuditorContext.set("testUser", UserRole.CUSTOMER);

        given(paymentRepository.findById(paymentId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPayment(paymentId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_NOT_FOUND);

        then(userClient).shouldHaveNoInteractions();
        then(paymentValidator).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("검색 조건에 따른 결제 목록 조회 성공 - CUSTOMER")
    void searchPayments_success_customer_forcesUserId() {
        AuditorContext.set("testUser", UserRole.CUSTOMER);

        UserRes userRes = UserRes.of(1L, "testUser", "testUser", "test@example.com", UserRole.CUSTOMER, "ACTIVE");
        given(userClient.getUserByLoginId("testUser")).willReturn(userRes);

        UUID orderId = UUID.randomUUID();
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        PageRequest pageRequest = PageRequest.of(0, 10);

        Payment payment1 = Payment.create(orderId, 1L, new BigDecimal("10000"), "KRW", PaymentMethod.CARD, "k1");
        ReflectionTestUtils.setField(payment1, "id", UUID.randomUUID());
        payment1.approve("k1");

        Page<Payment> paymentPage = new PageImpl<>(List.of(payment1), pageRequest, 1);

        given(paymentRepository.findPayments(
                eq(PaymentStatus.APPROVED),
                eq(1L),
                eq(orderId),
                eq(startDate),
                eq(endDate),
                eq(pageRequest)
        )).willReturn(paymentPage);

        FindPaymentListByConditionCommand command =
                new FindPaymentListByConditionCommand(
                        "APPROVED",
                        999L,
                        orderId,
                        startDate,
                        endDate
                );

        Page<ReadPaymentSummaryRes> result = paymentService.searchPayments(command, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(1L);

        then(paymentValidator).should().validateSearchPayments(eq(command), eq(userRes));
    }

    @Test
    @DisplayName("검색 조건에 따른 결제 목록 조회 성공 - MANAGER")
    void searchPayments_success_manager_usesCommandUserId() {
        AuditorContext.set("manager", UserRole.MANAGER);

        UserRes userRes = UserRes.of(10L, "manager", "manager", "manager@example.com", UserRole.MANAGER, "ACTIVE");
        given(userClient.getUserByLoginId("manager")).willReturn(userRes);

        UUID orderId = UUID.randomUUID();
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<Payment> emptyPage = new PageImpl<>(List.of(), pageRequest, 0);

        FindPaymentListByConditionCommand command =
                new FindPaymentListByConditionCommand(
                        null,
                        1L,
                        orderId,
                        null,
                        null
                );

        given(paymentRepository.findPayments(
                isNull(),
                eq(1L),
                eq(orderId),
                isNull(),
                isNull(),
                eq(pageRequest)
        )).willReturn(emptyPage);

        Page<ReadPaymentSummaryRes> result = paymentService.searchPayments(command, pageRequest);

        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();

        then(paymentValidator).should().validateSearchPayments(eq(command), eq(userRes));
    }

    @Test
    @DisplayName("검색 조건에 맞지 않으면 결제 목록 조회 실패 - 잘못된 상태값")
    void searchPayments_failure_invalidStatus() {
        AuditorContext.set("testUser", UserRole.CUSTOMER);

        UserRes userRes = UserRes.of(1L, "testUser", "testUser", "test@example.com", UserRole.CUSTOMER, "ACTIVE");
        given(userClient.getUserByLoginId("testUser")).willReturn(userRes);

        PageRequest pageRequest = PageRequest.of(0, 10);

        FindPaymentListByConditionCommand command =
                new FindPaymentListByConditionCommand(
                        "NONE",
                        null,
                        null,
                        null,
                        null
                );

        assertThatThrownBy(() -> paymentService.searchPayments(command, pageRequest))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.INVALID_PAYMENT_STATUS);

        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("PENDING 상태 결제 삭제 성공 - MANAGER 권한")
    void deletePayment_success_pending_manager() {
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        AuditorContext.set("manager", UserRole.MANAGER);

        Payment payment = Payment.createPending(orderId, 1L, new BigDecimal("10000"));
        ReflectionTestUtils.setField(payment, "id", paymentId);

        UserRes userRes = UserRes.of(10L, "manager", "manager", "manager@example.com", UserRole.MANAGER, "ACTIVE");

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
        given(userClient.getUserByLoginId("manager")).willReturn(userRes);
        given(paymentRepository.save(any(Payment.class))).willAnswer(invocation -> invocation.getArgument(0));

        paymentService.deletePayment(paymentId);

        then(paymentValidator).should().validateDeletePayment(eq(payment), eq(userRes));
        then(paymentRepository).should(times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("존재하지 않는 paymentId로 인해 결제 삭제 실패")
    void deletePayment_failure_paymentNotFound() {
        UUID paymentId = UUID.randomUUID();

        AuditorContext.set("manager", UserRole.MANAGER);

        given(paymentRepository.findById(paymentId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.deletePayment(paymentId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_NOT_FOUND);

        then(userClient).shouldHaveNoInteractions();
        then(paymentValidator).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("PENDING 상태가 아닌 결제 삭제 실패")
    void deletePayment_failure_notPending() {
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        AuditorContext.set("manager", UserRole.MANAGER);

        Payment payment = Payment.createPending(orderId, 1L, new BigDecimal("10000"));
        ReflectionTestUtils.setField(payment, "id", paymentId);
        payment.approve("k1");

        UserRes userRes = UserRes.of(10L, "manager", "manager", "manager@example.com", UserRole.MANAGER, "ACTIVE");

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
        given(userClient.getUserByLoginId("manager")).willReturn(userRes);

        willThrow(new BusinessException(PaymentErrorCode.PAYMENT_CANNOT_BE_DELETED))
                .given(paymentValidator).validateDeletePayment(eq(payment), eq(userRes));

        assertThatThrownBy(() -> paymentService.deletePayment(paymentId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_CANNOT_BE_DELETED);

        then(paymentRepository).should(never()).save(any(Payment.class));
    }
}
