package com.palja.user_service.infrastructure.external.kafka;

import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.palja.user_service.application.event.dto.UserEvent;
import com.palja.user_service.application.event.publisher.UserEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserKafkaProducer implements UserEventPublisher {

	private final KafkaTemplate<String, UserEvent> kafkaTemplate;

	@Override
	public void publish(UserEvent event) {
		CompletableFuture<SendResult<String, UserEvent>> future = kafkaTemplate.send(event.topic(), event);
		future.whenComplete((result, e) -> {
			if (e == null) {
				log.info("이벤트가 발행되었습니다. topic={}, partition={}, offset={}",
					result.getRecordMetadata().topic(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
			} else {
				log.error("이벤트 발행 중 예외가 발생했습니다. topic={}", event.topic(), e);
			}
		});
	}

}
