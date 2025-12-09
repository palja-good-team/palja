package com.palja.payment_service.application.service.impl;

import com.palja.common.auditor.CurrentUser;
import com.palja.common.exception.BusinessException;
import com.palja.payment_service.application.command.FindPaymentLogListByConditionCommand;
import com.palja.payment_service.application.dto.response.ReadPaymentLogRes;
import com.palja.payment_service.application.dto.response.UserRes;
import com.palja.payment_service.application.service.PaymentLogService;
import com.palja.payment_service.application.service.UserService;
import com.palja.payment_service.application.validator.PaymentValidator;
import com.palja.payment_service.domain.entity.PaymentLog;
import com.palja.payment_service.domain.repository.PaymentLogRepository;
import com.palja.payment_service.domain.vo.PaymentStatus;
import com.palja.payment_service.exception.PaymentErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentLogServiceImpl implements PaymentLogService {

    private final PaymentLogRepository paymentLogRepository;
    private final PaymentValidator paymentValidator;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public List<ReadPaymentLogRes> getLogsByPaymentId(UUID paymentId) {

        String loginId = CurrentUser.getLoginId();
        UserRes user = userService.getUserByLoginId(loginId);

        paymentValidator.validateGetPaymentLogs(paymentId, user);

        List<PaymentLog> logs = paymentLogRepository.findByPaymentId(paymentId);
        if (logs.isEmpty()) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_LOG_NOT_FOUND);
        }
        return logs.stream()
                .map(ReadPaymentLogRes::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReadPaymentLogRes> searchLogs(FindPaymentLogListByConditionCommand command,
                                              PageRequest pageRequest) {

        String loginId = CurrentUser.getLoginId();
        UserRes user = userService.getUserByLoginId(loginId);

        paymentValidator.validateSearchPaymentLogs(command.startDate(), command.endDate(), user);

        PaymentStatus status = null;
        if (command.status() != null) {
            try {
                status = PaymentStatus.valueOf(command.status());
            } catch (IllegalArgumentException e) {
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

        return logs.map(ReadPaymentLogRes::from);
    }

    @Override
    @Transactional
    public void deleteOldLogs(){
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

        paymentValidator.validateDeleteOldLogs(oneYearAgo);

        paymentLogRepository.deleteLogsOlder(oneYearAgo);
    }
}