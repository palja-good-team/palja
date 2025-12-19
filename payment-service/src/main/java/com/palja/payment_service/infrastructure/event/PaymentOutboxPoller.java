package com.palja.payment_service.infrastructure.event;

import com.palja.payment_service.application.event.PaymentEventPublisher;
import com.palja.payment_service.domain.event.vo.OutboxStatus;
import com.palja.payment_service.domain.repository.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentOutboxPoller {

    private final PaymentOutboxRepository outboxRepository;
    private final PaymentEventPublisher paymentEventPublisher;

    @Scheduled(fixedDelayString = "${app.outbox.publish-interval-ms:1000}")
    public void publishPendingEvents() {
        var pending = outboxRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        for (var outbox : pending) {
            paymentEventPublisher.publish(outbox);
        }
    }
}
