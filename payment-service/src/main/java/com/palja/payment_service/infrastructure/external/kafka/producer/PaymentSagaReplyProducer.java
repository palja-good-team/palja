package com.palja.payment_service.infrastructure.external.kafka.producer;

import com.palja.payment_service.application.event.dto.response.PaymentCreateEventRes;
import com.palja.payment_service.infrastructure.external.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSagaReplyProducer {

    @Qualifier("sagaKafkaTemplate")
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishCreateSuccess(PaymentCreateEventRes res) {
        sendSync(KafkaTopics.PAYMENT_CREATE_SUCCESS, res.getSagaId().toString(), res);
        log.info("saga 결제 응답 생성 성공: sagaId={}, orderId={}, paymentId={}",
                res.getSagaId(), res.getOrderId(), res.getPaymentId());
    }

    public void publishCreateFailure(PaymentCreateEventRes res) {
        sendSync(KafkaTopics.PAYMENT_CREATE_FAILURE, res.getSagaId().toString(), res);
        log.info("saga 결제 응답 생성 실패: sagaId={}, orderId={}",
                res.getSagaId(), res.getOrderId());
    }

    private void sendSync(String topic, String key, Object payload) {
        try {
            kafkaTemplate.send(topic, key, payload).get();
        } catch (Exception e) {
            throw new RuntimeException("Kafka publish failed. topic=" + topic + ", key=" + key, e);
        }
    }
}
