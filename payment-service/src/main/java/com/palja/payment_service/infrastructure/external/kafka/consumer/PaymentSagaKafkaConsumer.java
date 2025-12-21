package com.palja.payment_service.infrastructure.external.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.common.auditor.AuditorContext;
import com.palja.common.exception.BusinessException;
import com.palja.payment_service.application.service.PaymentSagaService;
import com.palja.payment_service.application.event.dto.request.PaymentCancelEventReq;
import com.palja.payment_service.application.event.dto.request.PaymentCreateEventReq;
import com.palja.payment_service.application.event.dto.response.PaymentCreateEventRes;
import com.palja.payment_service.infrastructure.external.kafka.KafkaTopics;
import com.palja.payment_service.infrastructure.external.kafka.producer.PaymentSagaReplyProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSagaKafkaConsumer {

    private final ObjectMapper objectMapper;
    private final PaymentSagaService paymentSagaService;
    private final PaymentSagaReplyProducer replyProducer;

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_CREATE_REQUEST,
            containerFactory = "sagaKafkaListenerContainerFactory"
    )
    public void onPaymentCreateRequest(ConsumerRecord<String, Object> record, Acknowledgment ack) {
        PaymentCreateEventReq req = objectMapper.convertValue(record.value(), PaymentCreateEventReq.class);

        log.info("saga 결제 생성 요청: sagaId={}, orderId={}, userId={}, amount={}",
                req.getSagaId(), req.getOrderId(), req.getUserId(), req.getAmount());

        try {
            UUID paymentId = paymentSagaService.handleCreate(req);

            replyProducer.publishCreateSuccess(
                    new PaymentCreateEventRes(req.getSagaId(), req.getOrderId(), paymentId)
            );

            ack.acknowledge();

        } catch (BusinessException be) {
            log.warn("saga 결제 생성 요청 비즈니스 예외로 실패: sagaId={}, orderId={}, msg={}",
                    req.getSagaId(), req.getOrderId(), be.getMessage());

            replyProducer.publishCreateFailure(
                    new PaymentCreateEventRes(req.getSagaId(), req.getOrderId(), null)
            );
            ack.acknowledge();

        } catch (Exception e) {
            log.error("saga 결제 생성 요청 시스템 장애로 실패: sagaId={}, orderId={}",
                    req.getSagaId(), req.getOrderId(), e);
            throw e;
        } finally {
            AuditorContext.clear();
        }
    }

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_CANCEL_REQUEST,
            containerFactory = "sagaKafkaListenerContainerFactory"
    )
    public void onPaymentCancelRequest(ConsumerRecord<String, Object> record, Acknowledgment ack) {
        PaymentCancelEventReq req = objectMapper.convertValue(record.value(), PaymentCancelEventReq.class);

        log.info("saga 결제 취소 요청: sagaId={}, orderId={}, paymentId={}, amount={}",
                req.getSagaId(), req.getOrderId(), req.getPaymentId(), req.getAmount());

        try {
            paymentSagaService.handleCancel(req);

            ack.acknowledge();

        } catch (BusinessException be) {
            log.warn("saga 결제 취소 요청 비즈니스 예외로 실패: sagaId={}, orderId={}, msg={}",
                    req.getSagaId(), req.getOrderId(), be.getMessage());
            ack.acknowledge();

        } catch (Exception e) {
            log.error("saga 결제 취소 요청 시스템 장애로 실패: sagaId={}, orderId={}",
                    req.getSagaId(), req.getOrderId(), e);
            throw e;
        } finally {
            AuditorContext.clear();
        }
    }
}
