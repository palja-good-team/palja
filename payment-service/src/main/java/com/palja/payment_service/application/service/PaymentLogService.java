package com.palja.payment_service.application.service;

import com.palja.payment_service.application.command.FindPaymentLogListByConditionCommand;
import com.palja.payment_service.application.dto.response.ReadPaymentLogRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

public interface PaymentLogService {
    List<ReadPaymentLogRes> getLogsByPaymentId(UUID paymentId);

    Page<ReadPaymentLogRes> searchLogs(FindPaymentLogListByConditionCommand command, PageRequest pageRequest);

    void deleteOldLogs();
}
