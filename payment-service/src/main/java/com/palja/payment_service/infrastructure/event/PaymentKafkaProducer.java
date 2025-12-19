package com.palja.payment_service.infrastructure.event;

import com.palja.payment_service.application.event.PaymentEventPublisher;
import com.palja.payment_service.domain.entity.PaymentOutbox;
import com.palja.payment_service.domain.event.vo.PaymentEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaProducer implements PaymentEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxStatusUpdater outboxStatusUpdater;

    @Override
    public void publish(PaymentOutbox outbox) {
        try {
            String topic = resolveTopic(outbox);

            ProducerRecord<String, String> record =
                    new ProducerRecord<>(topic, outbox.getOrderId().toString(), outbox.getPayload());

            addHeaderIfPresent(record, "X-USER-LOGIN-ID", outbox.getLoginId());

            if (outbox.getUserRole() != null) {
                record.headers().add(
                        "X-USER-ROLE",
                        outbox.getUserRole().name().getBytes(StandardCharsets.UTF_8)
                );
            }

            addHeaderIfPresent(record, "X-TRACE-ID", outbox.getTraceId());
            addHeaderIfPresent(record, "X-SPAN-ID", outbox.getSpanId());

            kafkaTemplate.send(record).get();

            outboxStatusUpdater.markSent(outbox.getId());

            log.info("Outbox 발행 성공: outboxId={}, topic={}, type={}, paymentId={}, orderId={}",
                    outbox.getId(), topic, outbox.getEventType(), outbox.getAggregateId(), outbox.getOrderId());

        } catch (Exception e) {
            outboxStatusUpdater.markFailed(outbox.getId(), e.getMessage());

            log.error("Outbox 발행 실패: outboxId={}, type={}, error={}",
                    outbox.getId(), outbox.getEventType(), e.getMessage(), e);
        }
    }

    private String resolveTopic(PaymentOutbox outbox) {
        PaymentEventType type = outbox.getEventType();
        return switch (type) {
            case PAYMENT_APPROVED -> KafkaTopics.PAYMENT_ORDER_APPROVE_SUCCESS;
            case PAYMENT_FAILED -> KafkaTopics.PAYMENT_ORDER_APPROVE_FAILURE;
            case PAYMENT_CANCELED -> KafkaTopics.PAYMENT_ORDER_CANCEL_SUCCESS;
            case PAYMENT_CANCEL_FAILED -> KafkaTopics.PAYMENT_ORDER_CANCEL_FAILURE;
        };
    }

    private void addHeaderIfPresent(ProducerRecord<String, String> record, String key, String value) {
        if (value != null && !value.isBlank()) {
            record.headers().add(key, value.getBytes(StandardCharsets.UTF_8));
        }
    }
}
