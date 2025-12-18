package com.palja.payment_service.infrastructure.event;

import com.palja.payment_service.domain.entity.PaymentOutbox;
import com.palja.payment_service.domain.repository.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OutboxStatusUpdater {

    private final PaymentOutboxRepository outboxRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markSent(Long outboxId) {
        PaymentOutbox outbox = outboxRepository.findById(outboxId).orElseThrow();
        outbox.markSent();
        outboxRepository.save(outbox);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(Long outboxId, String message) {
        PaymentOutbox outbox = outboxRepository.findById(outboxId).orElseThrow();
        outbox.markFailed(message);
        outboxRepository.save(outbox);
    }
}
