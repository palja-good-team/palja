package com.palja.coupon_service.infrastructure.external.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.common.auditor.CurrentUser;
import com.palja.coupon_service.application.command.UseCouponCommand;
import com.palja.coupon_service.application.event.dto.request.CouponCancelEventReq;
import com.palja.coupon_service.application.event.dto.request.CouponUseEventReq;
import com.palja.coupon_service.application.event.dto.response.CouponCancelEventRes;
import com.palja.coupon_service.application.event.dto.response.CouponUseEventRse;
import com.palja.coupon_service.application.service.CouponService;
import com.palja.coupon_service.infrastructure.external.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponKafkaConsumer {

    private final CouponService couponService;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = KafkaTopics.COUPON_USE_REQUEST)
    public void useCoupon(ConsumerRecord<String, Object> record) {
        CouponUseEventReq event = objectMapper.convertValue(record.value(), CouponUseEventReq.class);

        log.info("쿠폰 사용 요청 이벤트 수신 - sagaId: {}, orderId: {}, couponUserId: {}",
                event.getSagaId(), event.getOrderId(), event.getCouponUserId());

        try {
            UseCouponCommand command = new UseCouponCommand(
                    event.getCouponUserId(),
                    CurrentUser.getLoginId(),
                    event.getOrderId(),
                    event.getDiscountAmount().longValue()
            );

            couponService.useCoupon(command);

            CouponUseEventRse response = CouponUseEventRse.of(event.getSagaId(), event.getOrderId());

            kafkaTemplate.send(KafkaTopics.COUPON_USE_SUCCESS, response);
            log.info("쿠폰 사용 성공 - sagaId: {}, orderId: {}, couponUserId: {}",
                    event.getSagaId(), event.getOrderId(), event.getCouponUserId());

        } catch (Exception e) {
            CouponUseEventRse response = CouponUseEventRse.of(event.getSagaId(), event.getOrderId());

            kafkaTemplate.send(KafkaTopics.COUPON_USE_FAILURE, response);

            log.error("쿠폰 사용 실패 - sagaId: {}, orderId: {}, couponUserId: {}",
                    event.getSagaId(), event.getOrderId(), event.getCouponUserId(), e);
        }
    }

    @KafkaListener(topics = KafkaTopics.COUPON_CANCEL_REQUEST)
    public void cancelCoupon(ConsumerRecord<String, Object> record) {
        CouponCancelEventReq event = objectMapper.convertValue(record.value(), CouponCancelEventReq.class);

        log.info("쿠폰 취소 요청 이벤트 수신 - sagaId: {}, orderId: {}, couponUserId: {}",
                event.getSagaId(), event.getOrderId(), event.getCouponUserId());

        try {
            couponService.cancelCoupon(event.getCouponUserId(), CurrentUser.getLoginId());

            CouponCancelEventRes response = CouponCancelEventRes.of(event.getSagaId(), event.getOrderId());

            kafkaTemplate.send(KafkaTopics.COUPON_CANCEL_SUCCESS, response);
            log.info("쿠폰 취소 성공 - sagaId: {}, orderId: {}, couponUserId: {}",
                    event.getSagaId(), event.getOrderId(), event.getCouponUserId());

        } catch (Exception e) {
            CouponCancelEventRes response = CouponCancelEventRes.of(event.getSagaId(), event.getOrderId());

            kafkaTemplate.send(KafkaTopics.COUPON_CANCEL_FAILURE, response);

            log.error("쿠폰 취소 실패 - sagaId: {}, orderId: {}, couponUserId: {}",
                    event.getSagaId(), event.getOrderId(), event.getCouponUserId(), e);
        }

    }
}
