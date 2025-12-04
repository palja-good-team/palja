package com.palja.payment_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.payment_service.application.command.FindPaymentLogListByConditionCommand;
import com.palja.payment_service.application.dto.response.PaymentLogDetailRes;
import com.palja.payment_service.application.service.PaymentLogService;
import com.palja.payment_service.domain.entity.PaymentLog;
import com.palja.payment_service.domain.repository.PaymentLogRepository;
import com.palja.payment_service.domain.vo.PaymentStatus;
import com.palja.payment_service.exception.PaymentErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentLogServiceImpl implements PaymentLogService {

    private final PaymentLogRepository paymentLogRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentLogDetailRes> getLogsByPaymentId(UUID paymentId) {
        List<PaymentLog> logs = paymentLogRepository.findByPaymentId(paymentId);
        if (logs.isEmpty()) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_LOG_NOT_FOUND);
        }
        return logs.stream()
                .map(PaymentLogDetailRes::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentLogDetailRes> searchLogs(FindPaymentLogListByConditionCommand command,
                                                PageRequest pageRequest) {

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
                status,
                command.startDate(),
                command.endDate(),
                pageRequest
        );

        return logs.map(PaymentLogDetailRes::from);
    }
}