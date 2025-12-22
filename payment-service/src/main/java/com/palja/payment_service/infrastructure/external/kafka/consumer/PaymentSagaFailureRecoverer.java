package com.palja.payment_service.infrastructure.external.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.payment_service.application.event.dto.request.PaymentCancelEventReq;
import com.palja.payment_service.application.event.dto.request.PaymentCreateEventReq;
import com.palja.payment_service.application.event.dto.response.PaymentCreateEventRes;
import com.palja.payment_service.infrastructure.external.kafka.KafkaTopics;
import com.palja.payment_service.infrastructure.external.kafka.producer.PaymentSagaReplyProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSagaFailureRecoverer implements ConsumerRecordRecoverer {

    private final ObjectMapper objectMapper;
    private final PaymentSagaReplyProducer replyProducer;

    @Override
    public void accept(ConsumerRecord<?, ?> record, Exception ex) {
        String topic = record.topic();

        if (KafkaTopics.PAYMENT_CREATE_REQUEST.equals(topic)) {
            PaymentCreateEventReq req = objectMapper.convertValue(record.value(), PaymentCreateEventReq.class);

            replyProducer.publishCreateFailure(
                    new PaymentCreateEventRes(req.getSagaId(), req.getOrderId(), null)
            );

            return;
        }

        if (KafkaTopics.PAYMENT_CANCEL_REQUEST.equals(topic)) {
            PaymentCancelEventReq req = objectMapper.convertValue(record.value(), PaymentCancelEventReq.class);
            log.error("saga 결제 취소 요청 실패: sagaId={}, orderId={}, paymentId={}, error={}",
                    req.getSagaId(), req.getOrderId(), req.getPaymentId(), ex.getMessage());
            return;
        }
    }
}
