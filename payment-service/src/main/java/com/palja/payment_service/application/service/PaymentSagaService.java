package com.palja.payment_service.application.service;

import com.palja.payment_service.infrastructure.saga.dto.request.PaymentCancelEventReq;
import com.palja.payment_service.infrastructure.saga.dto.request.PaymentCreateEventReq;

import java.util.UUID;

public interface PaymentSagaService {
    UUID handleCreate(PaymentCreateEventReq req);
    void handleCancel(PaymentCancelEventReq req);
}
