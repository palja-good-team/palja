package com.palja.payment_service.application.service.impl;

import com.palja.common.auditor.AuditorContext;
import com.palja.common.exception.BusinessException;
import com.palja.common.vo.UserRole;
import com.palja.payment_service.application.command.FindPaymentLogListByConditionCommand;
import com.palja.payment_service.application.dto.response.ReadPaymentLogRes;
import com.palja.payment_service.application.dto.response.UserRes;
import com.palja.payment_service.application.service.UserService;
import com.palja.payment_service.application.validator.PaymentValidator;
import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.entity.PaymentLog;
import com.palja.payment_service.domain.repository.PaymentLogRepository;
import com.palja.payment_service.domain.vo.PaymentMethod;
import com.palja.payment_service.domain.vo.PaymentStatus;
import com.palja.payment_service.exception.PaymentErrorCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentLogServiceImplTest {

    @Mock
    private PaymentLogRepository paymentLogRepository;

    @Spy
    private PaymentValidator paymentValidator;

    @Mock
    private UserService userService;

    @InjectMocks
    private PaymentLogServiceImpl paymentLogService;

    private PaymentLog createLog(UUID paymentId, PaymentStatus status) {
        Payment payment = Payment.create(
                UUID.randomUUID(),
                1L,
                new BigDecimal("10000"),
                "KRW",
                PaymentMethod.CARD,
                "paymentKey123"
        );

        if (status == PaymentStatus.APPROVED) {
            payment.approve("paymentKey123");
        } else if (status == PaymentStatus.FAILED) {
            payment.fail("실패");
        }

        ReflectionTestUtils.setField(payment, "id", paymentId);

        return PaymentLog.createResultLog(
                payment,
                "paymentKey123",
                "SUCCESS",
                "성공"
        );
    }

    private void setCurrentUser(String loginId, UserRole role) {
        AuditorContext.set(loginId, role);
    }

    @AfterEach
    void tearDown() {
        AuditorContext.clear();
    }

    @Test
    @DisplayName("paymentId 기준 전체 로그 반환 성공 - MASTER 권한")
    void getLogsByPaymentId_success_master() {
        UUID paymentId = UUID.randomUUID();
        PaymentLog log1 = createLog(paymentId, PaymentStatus.APPROVED);
        PaymentLog log2 = createLog(paymentId, PaymentStatus.FAILED);

        setCurrentUser("master", UserRole.MASTER);

        UserRes userRes = UserRes.of(
                1L,
                "master",
                "master",
                "master@example.com",
                UserRole.MASTER,
                "ACTIVE"
        );

        given(userService.getUserByLoginId(any())).willReturn(userRes);
        given(paymentLogRepository.findByPaymentId(paymentId))
                .willReturn(List.of(log1, log2));

        List<ReadPaymentLogRes> result = paymentLogService.getLogsByPaymentId(paymentId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPaymentId()).isEqualTo(paymentId);
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

        given(userService.getUserByLoginId(any())).willReturn(userRes);

        assertThatThrownBy(() -> paymentLogService.getLogsByPaymentId(paymentId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.INVALID_PAYMENT_STATUS);
    }

    @Test
    @DisplayName("해당 paymentId 로그가 없다면 결제 로그 조회 실패")
    void getLogsByPaymentId_failure_notFound() {
        UUID paymentId = UUID.randomUUID();

        setCurrentUser("master", UserRole.MASTER);

        UserRes userRes = UserRes.of(
                1L,
                "master",
                "master",
                "master@example.com",
                UserRole.MASTER,
                "ACTIVE"
        );

        given(userService.getUserByLoginId(any())).willReturn(userRes);
        given(paymentLogRepository.findByPaymentId(paymentId))
                .willReturn(List.of());

        assertThatThrownBy(() -> paymentLogService.getLogsByPaymentId(paymentId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.PAYMENT_LOG_NOT_FOUND);
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

        given(userService.getUserByLoginId(any())).willReturn(userRes);
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

        given(userService.getUserByLoginId(any())).willReturn(userRes);

        FindPaymentLogListByConditionCommand command =
                new FindPaymentLogListByConditionCommand(
                        null,
                        null,
                        null,
                        null,
                        null
                );

        assertThatThrownBy(() -> paymentLogService.searchLogs(command, pageRequest))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.INVALID_PAYMENT_STATUS);
    }

    @Test
    @DisplayName("검색 조건에 맞지 않으면 결제 로그 목록 조회 실패 - 잘못된 상태값")
    void searchLogs_failure_invalidStatus() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        setCurrentUser("master", UserRole.MASTER);

        UserRes userRes = UserRes.of(
                1L,
                "master",
                "master",
                "master@example.com",
                UserRole.MASTER,
                "ACTIVE"
        );

        given(userService.getUserByLoginId(any())).willReturn(userRes);

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

        verify(paymentLogRepository).deleteLogsOlder(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("1년 이상 된 결제 로그가 없을 경우에도 정상 처리")
    void deleteOldLogs_success_noLogsToDelete() {
        paymentLogService.deleteOldLogs();

        verify(paymentLogRepository).deleteLogsOlder(any(LocalDateTime.class));
    }
}
