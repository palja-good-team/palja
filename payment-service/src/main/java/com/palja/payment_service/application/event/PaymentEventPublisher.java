package com.palja.payment_service.application.event;

import com.palja.payment_service.domain.entity.PaymentOutbox;

public interface PaymentEventPublisher {
    void publish(PaymentOutbox outbox);
}
