package com.palja.payment_service.application.service.impl;

import com.palja.common.auditor.CurrentUser;
import com.palja.common.exception.BusinessException;
import com.palja.payment_service.application.command.FindPaymentLogListByConditionCommand;
import com.palja.payment_service.application.dto.response.ReadPaymentLogRes;
import com.palja.payment_service.application.dto.external.UserRes;
import com.palja.payment_service.application.service.PaymentLogService;
import com.palja.payment_service.application.port.UserClient;
import com.palja.payment_service.application.validator.PaymentValidator;
import com.palja.payment_service.domain.entity.PaymentLog;
import com.palja.payment_service.domain.repository.PaymentLogRepository;
import com.palja.payment_service.domain.vo.PaymentStatus;
import com.palja.payment_service.exception.PaymentErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentLogServiceImpl implements PaymentLogService {

    private final PaymentLogRepository paymentLogRepository;
    private final PaymentValidator paymentValidator;
    private final UserClient userClient;

    @Override
    @Transactional(readOnly = true)
    public List<ReadPaymentLogRes> getLogsByPaymentId(UUID paymentId) {

        String loginId = CurrentUser.getLoginId();
        UserRes user = userClient.getUserByLoginId(loginId);

        log.info("결제 로그 조회 시작: paymentId={}, requestUserId={}, role={}",
                paymentId,
                user != null ? user.getUserId() : null,
                user != null ? user.getRole() : null);

        paymentValidator.validateGetPaymentLogs(paymentId, user);

        List<PaymentLog> logs = paymentLogRepository.findByPaymentId(paymentId);
        if (logs.isEmpty()) {
            log.warn("결제 로그 없음: paymentId={}, requestUserId={}",
                    paymentId,
                    user != null ? user.getUserId() : null);
            throw new BusinessException(PaymentErrorCode.PAYMENT_LOG_NOT_FOUND);
        }
        log.info("결제 로그 조회 완료(paymentId 기준): paymentId={}, count={}, requestUserId={}, role={}",
                paymentId, logs.size(),
                user != null ? user.getUserId() : null,
                user != null ? user.getRole() : null);

        return logs.stream()
                .map(ReadPaymentLogRes::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReadPaymentLogRes> searchLogs(FindPaymentLogListByConditionCommand command,
                                              PageRequest pageRequest) {

        String loginId = CurrentUser.getLoginId();
        UserRes user = userClient.getUserByLoginId(loginId);

        log.info("결제 로그 검색 시작: paymentId={}, orderId={}, status={}, startDate={}, endDate={}, page={}, size={}, requestUserId={}, role={}",
                command.paymentId(),
                command.orderId(),
                command.status(),
                command.startDate(),
                command.endDate(),
                pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                user != null ? user.getUserId() : null,
                user != null ? user.getRole() : null);

        paymentValidator.validateSearchPaymentLogs(command.startDate(), command.endDate(), user);

        PaymentStatus status = null;
        if (command.status() != null) {
            try {
                status = PaymentStatus.valueOf(command.status());
            } catch (IllegalArgumentException e) {
                log.warn("결제 로그 검색 실패: 유효하지 않은 status 값. status={}, paymentId={}, orderId={}, requestUserId={}",
                        command.status(), command.paymentId(), command.orderId(),
                        user != null ? user.getUserId() : null);
                throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
            }
        }

        Page<PaymentLog> logs = paymentLogRepository.findLogs(
                command.paymentId(),
                command.orderId(),
                status,
                command.startDate(),
                command.endDate(),
                pageRequest
        );

        log.info("결제 로그 검색 완료: totalElements={}, totalPages={}, page={}, size={}, requestUserId={}",
                logs.getTotalElements(), logs.getTotalPages(), logs.getNumber(), logs.getSize(),
                user != null ? user.getUserId() : null);

        return logs.map(ReadPaymentLogRes::from);
    }

    @Override
    @Transactional
    public void deleteOldLogs(){
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

        log.info("결제 로그 삭제 시작: cutoffDate={}", oneYearAgo);

        paymentValidator.validateDeleteOldLogs(oneYearAgo);
        paymentLogRepository.deleteLogsOlder(oneYearAgo);

        log.info("결제 로그 삭제 완료: cutoffDate={}", oneYearAgo);
    }
}