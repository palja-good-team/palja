package com.palja.user_service.infrastructure.external.kafka.producer;

import static com.palja.user_service.infrastructure.external.kafka.KafkaTopics.*;

import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.palja.user_service.application.event.dto.request.DeleteCompanyUserEventReq;
import com.palja.user_service.application.event.dto.request.DeleteCustomerEventReq;
import com.palja.user_service.application.port.UserEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserKafkaProducer implements UserEventPublisher {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	/// 이벤트 발행
	private void publish(String topic, Object event) {
		CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, event);

		future.whenComplete((result, e) -> {
			if (e == null) {
				log.info("이벤트가 발행되었습니다. topic={}, partition={}, offset={}",
					result.getRecordMetadata().topic(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
			} else {
				log.error("이벤트 발행 중 예외가 발생했습니다. topic={}", topic, e);
			}
		});
	}

	@Override
	public void publishCustomerDeleteEvent(DeleteCustomerEventReq event) {
		publish(CUSTOMER_DELETE_REQUEST_TOPIC, event);
	}

	@Override
	public void publishCompanyUserDeleteEvent(DeleteCompanyUserEventReq event) {
		publish(COMPANY_USER_DELETE_REQUEST_TOPIC, event);
	}

}
