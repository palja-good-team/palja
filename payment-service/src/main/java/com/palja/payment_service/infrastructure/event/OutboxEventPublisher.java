package com.palja.payment_service.infrastructure.event;

import com.palja.payment_service.application.event.OutboxStatus;
import com.palja.payment_service.domain.entity.PaymentOutbox;
import com.palja.payment_service.domain.repository.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final PaymentOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    /*
    PENDING outbox를 polling 해서 Kafka로 발행
    성공시 outboxStatus는 SENT로 변경, sentAt 기록
    실패시 outboxStatus는 FAILED로 변경, retryCount++ 되고 errorMessage 기록
     */
    @Scheduled(fixedDelayString = "${app.outbox.publish-interval-ms:1000}")
    @Transactional
    public void publishPendingEvents(){
        List<PaymentOutbox> pendingEvents = outboxRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        if (pendingEvents.isEmpty()){
            return;
        }

        log.info("Outbox 발행 시작: size={}", pendingEvents.size());

        for (PaymentOutbox outbox : pendingEvents){
            try {
                kafkaTemplate.send(
                        KafkaTopics.PAYMENT_EVENTS,
                        outbox.getOrderId().toString(),
                        outbox.getPayload()
                ).get();

                outbox.markSent();
                outboxRepository.save(outbox);

                log.info("Outbox 발행 성공: outboxId={}, type={}, paymentId={}, orderId={}",
                        outbox.getId(), outbox.getEventType(), outbox.getAggregateId(), outbox.getOrderId());
            } catch (Exception e) {
                outbox.markFailed(e.getMessage());
                outboxRepository.save(outbox);

                log.error("Outbox 발행 실패: outboxId={}, type={}, retryCount={}, error={}",
                        outbox.getId(), outbox.getEventType(), outbox.getRetryCount(), e.getMessage(), e);
            }
        }
    }
}
