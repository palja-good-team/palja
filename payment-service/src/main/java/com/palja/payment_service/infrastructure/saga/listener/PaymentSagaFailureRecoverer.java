package com.palja.payment_service.infrastructure.saga.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.payment_service.infrastructure.saga.dto.request.PaymentCreateEventReq;
import com.palja.payment_service.infrastructure.saga.dto.response.PaymentCreateEventRes;
import com.palja.payment_service.infrastructure.saga.producer.PaymentSagaReplyProducer;
import com.palja.payment_service.infrastructure.saga.topics.OrderSagaKafkaTopics;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentSagaFailureRecoverer implements ConsumerRecordRecoverer {

    private final ObjectMapper objectMapper;
    private final PaymentSagaReplyProducer replyProducer;

    @Override
    public void accept(ConsumerRecord<?, ?> record, Exception ex) {
        String topic = record.topic();

        if (OrderSagaKafkaTopics.PAYMENT_CREATE_REQUEST.equals(topic)) {
            PaymentCreateEventReq req = objectMapper.convertValue(record.value(), PaymentCreateEventReq.class);

            replyProducer.publishCreateFailure(
                    new PaymentCreateEventRes(req.getSagaId(), req.getOrderId(), null)
            );

            return;
        }

        if (OrderSagaKafkaTopics.PAYMENT_CANCEL_REQUEST.equals(topic)) {
            return;
        }
    }
}
