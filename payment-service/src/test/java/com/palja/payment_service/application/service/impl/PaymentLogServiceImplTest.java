package com.palja.payment_service.application.service.impl;

import com.palja.common.auditor.AuditorContext;
import com.palja.common.exception.BusinessException;
import com.palja.common.vo.UserRole;
import com.palja.payment_service.application.command.FindPaymentLogListByConditionCommand;
import com.palja.payment_service.application.dto.external.UserRes;
import com.palja.payment_service.application.dto.response.PGPaymentRes;
import com.palja.payment_service.application.dto.response.ReadPaymentLogRes;
import com.palja.payment_service.application.port.UserClient;
import com.palja.payment_service.application.validator.PaymentValidator;
import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.entity.PaymentLog;
import com.palja.payment_service.domain.repository.PaymentLogRepository;
import com.palja.payment_service.domain.vo.PaymentStatus;
import com.palja.payment_service.exception.PaymentErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentLogServiceImplTest {

    @Mock
    private PaymentLogRepository paymentLogRepository;

    @Mock
    private PaymentValidator paymentValidator;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private PaymentLogServiceImpl paymentLogService;

    private PaymentLog createLog(UUID paymentId, PaymentStatus status) {
        Payment payment = Payment.createPending(UUID.randomUUID(), 1L, new BigDecimal("10000"));
        ReflectionTestUtils.setField(payment, "id", paymentId);

        PGPaymentRes pgRes = PGPaymentRes.builder()
                .paymentKey("paymentKey123")
                .pgResponseCode(status == PaymentStatus.FAILED ? "ERROR" : "SUCCESS")
                .pgResponseMessage(status == PaymentStatus.FAILED ? "실패" : "성공")
                .success(status != PaymentStatus.FAILED)
                .approvedAmount(new BigDecimal("10000"))
                .build();

        if (status == PaymentStatus.APPROVED) {
            payment.approve("paymentKey123");
            return PaymentLog.createApprovedLog(payment, pgRes);
        }
        if (status == PaymentStatus.FAILED) {
            payment.fail("실패");
            return PaymentLog.createFailedLog(payment, pgRes);
        }
        return PaymentLog.createPendingLog(payment);
    }

    private void setCurrentUser(String loginId, UserRole role) {
        AuditorContext.set(loginId, role);
    }

    @AfterEach
    void tearDown() {
        AuditorContext.clear();
    }

    @Test
    @DisplayName("paymentId 기준 전체 로그 반환 성공 - MANAGER 권한")
    void getLogsByPaymentId_success_manager() {
        UUID paymentId = UUID.randomUUID();
        PaymentLog log1 = createLog(paymentId, PaymentStatus.APPROVED);
        PaymentLog log2 = createLog(paymentId, PaymentStatus.FAILED);

        setCurrentUser("manager", UserRole.MANAGER);

        UserRes userRes = UserRes.of(
                1L,
                "manager",
                "manager",
                "manager@example.com",
                UserRole.MANAGER,
                "ACTIVE"
        );

        given(userClient.getUserByLoginId(eq("manager"))).willReturn(userRes);
        given(paymentLogRepository.findByPaymentId(paymentId))
                .willReturn(List.of(log1, log2));

        List<ReadPaymentLogRes> result = paymentLogService.getLogsByPaymentId(paymentId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPaymentId()).isEqualTo(paymentId);

        then(paymentValidator).should().validateGetPaymentLogs(eq(paymentId), eq(userRes));
    }

    @Test
    @DisplayName("CUSTOMER 권한으로 결제 로그 조회 시 실패")
    void getLogsByPaymentId_failure_customer() {
        UUID paymentId = UUID.randomUUID();

        setCurrentUser("customer", UserRole.CUSTOMER);

        UserRes userRes = UserRes.of(
                1L,
                "customer",
                "customer",
                "customer@example.com",
                UserRole.CUSTOMER,
                "ACTIVE"
        );

        given(userClient.getUserByLoginId(eq("customer"))).willReturn(userRes);

        willThrow(new BusinessException(PaymentErrorCode.PAYMENT_LOG_ACCESS_DENIED))
                .given(paymentValidator).validateGetPaymentLogs(eq(paymentId), eq(userRes));

        assertThatThrownBy(() -> paymentLogService.getLogsByPaymentId(paymentId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_LOG_ACCESS_DENIED);

        then(paymentValidator).should().validateGetPaymentLogs(eq(paymentId), eq(userRes));
        then(paymentLogRepository).should(never()).findByPaymentId(any());
    }

    @Test
    @DisplayName("해당 paymentId 로그가 없다면 결제 로그 조회 실패")
    void getLogsByPaymentId_failure_notFound() {
        UUID paymentId = UUID.randomUUID();

        setCurrentUser("manager", UserRole.MANAGER);

        UserRes userRes = UserRes.of(
                1L,
                "manager",
                "manager",
                "manager@example.com",
                UserRole.MANAGER,
                "ACTIVE"
        );

        given(userClient.getUserByLoginId(eq("manager"))).willReturn(userRes);
        given(paymentLogRepository.findByPaymentId(paymentId))
                .willReturn(List.of());

        assertThatThrownBy(() -> paymentLogService.getLogsByPaymentId(paymentId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_LOG_NOT_FOUND);

        then(paymentValidator).should().validateGetPaymentLogs(eq(paymentId), eq(userRes));
    }

    @Test
    @DisplayName("검색 조건에 맞는 결제 로그 목록 조회 성공 - MANAGER 권한")
    void searchLogs_success_manager() {
        UUID paymentId = UUID.randomUUID();
        PageRequest pageRequest = PageRequest.of(0, 10);
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();

        setCurrentUser("manager", UserRole.MANAGER);

        UserRes userRes = UserRes.of(
                1L,
                "manager",
                "manager",
                "manager@example.com",
                UserRole.MANAGER,
                "ACTIVE"
        );

        PaymentLog log1 = createLog(paymentId, PaymentStatus.APPROVED);
        PaymentLog log2 = createLog(paymentId, PaymentStatus.FAILED);

        Page<PaymentLog> page = new PageImpl<>(List.of(log1, log2), pageRequest, 2);

        given(userClient.getUserByLoginId(eq("manager"))).willReturn(userRes);
        given(paymentLogRepository.findLogs(
                eq(paymentId),
                eq((UUID) null),
                eq(PaymentStatus.APPROVED),
                eq(startDate),
                eq(endDate),
                eq(pageRequest)
        )).willReturn(page);

        FindPaymentLogListByConditionCommand command =
                new FindPaymentLogListByConditionCommand(
                        paymentId,
                        null,
                        "APPROVED",
                        startDate,
                        endDate
                );

        Page<ReadPaymentLogRes> result = paymentLogService.searchLogs(command, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getPaymentId()).isEqualTo(paymentId);

        then(paymentValidator).should().validateSearchPaymentLogs(eq(startDate), eq(endDate), eq(userRes));
    }

    @Test
    @DisplayName("CUSTOMER 권한으로 결제 로그 검색 시 실패")
    void searchLogs_failure_customer() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        setCurrentUser("customer", UserRole.CUSTOMER);

        UserRes userRes = UserRes.of(
                1L,
                "customer",
                "customer",
                "customer@example.com",
                UserRole.CUSTOMER,
                "ACTIVE"
        );

        given(userClient.getUserByLoginId(eq("customer"))).willReturn(userRes);

        FindPaymentLogListByConditionCommand command =
                new FindPaymentLogListByConditionCommand(
                        null,
                        null,
                        null,
                        null,
                        null
                );

        willThrow(new BusinessException(PaymentErrorCode.PAYMENT_LOG_ACCESS_DENIED))
                .given(paymentValidator).validateSearchPaymentLogs(eq(null), eq(null), eq(userRes));

        assertThatThrownBy(() -> paymentLogService.searchLogs(command, pageRequest))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_LOG_ACCESS_DENIED);

        then(paymentValidator).should().validateSearchPaymentLogs(eq(null), eq(null), eq(userRes));
        then(paymentLogRepository).should(never()).findLogs(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("검색 조건에 맞지 않으면 결제 로그 목록 조회 실패 - 잘못된 상태값")
    void searchLogs_failure_invalidStatus() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        setCurrentUser("manager", UserRole.MANAGER);

        UserRes userRes = UserRes.of(
                1L,
                "manager",
                "manager",
                "manager@example.com",
                UserRole.MANAGER,
                "ACTIVE"
        );

        given(userClient.getUserByLoginId(eq("manager"))).willReturn(userRes);

        FindPaymentLogListByConditionCommand command =
                new FindPaymentLogListByConditionCommand(
                        null,
                        null,
                        "NONE",
                        null,
                        null
                );

        assertThatThrownBy(() -> paymentLogService.searchLogs(command, pageRequest))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.INVALID_PAYMENT_STATUS);
    }

    @Test
    @DisplayName("1년 지난 결제 로그 삭제 성공")
    void deleteOldLogs_success() {
        paymentLogService.deleteOldLogs();

        verify(paymentValidator).validateDeleteOldLogs(any(LocalDateTime.class));
        verify(paymentLogRepository).deleteLogsOlder(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("1년 이상 된 결제 로그가 없을 경우에도 정상 처리")
    void deleteOldLogs_success_noLogsToDelete() {
        paymentLogService.deleteOldLogs();

        verify(paymentValidator).validateDeleteOldLogs(any(LocalDateTime.class));
        verify(paymentLogRepository).deleteLogsOlder(any(LocalDateTime.class));
    }
}
