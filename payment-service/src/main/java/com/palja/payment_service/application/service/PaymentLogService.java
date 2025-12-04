package com.palja.payment_service.application.service;

import com.palja.payment_service.application.command.FindPaymentLogListByConditionCommand;
import com.palja.payment_service.application.dto.response.PaymentLogDetailRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface PaymentLogService {
    PaymentLogDetailRes getLogByPaymentId(UUID paymentId);

    Page<PaymentLogDetailRes> searchLogs(FindPaymentLogListByConditionCommand command, PageRequest pageRequest);
}
