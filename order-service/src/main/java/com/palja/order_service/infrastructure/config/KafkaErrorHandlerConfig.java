package com.palja.order_service.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Kafka DLQ 설정
 * - DLT: 계속 실패해서 더 이상 처리 못하는 메시지를 따로 모아두는 Kafka 토픽
 * - 2초마다 3번까지 해보고 (전략)
 * - 그래도 안 되면 .DLT 토픽으로 보내버린다
 */
@Slf4j
@Configuration
public class KafkaErrorHandlerConfig {

    // 재시도 시간 간격 (2초)
    private static final long BACKOFF_MS = 2000L;
    // 추가 재시도 횟수 (최초 1회 + 재시도 3회 = 총 4회 시도)
    private static final long MAX_RETRY = 3L;

    @Bean
    public CommonErrorHandler kafkaErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        // DLT(Dead Letter Topic)로 메시지 전송
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                // DLT 토픽 이름 규칙: 원본토픽.DLT
                (record, ex) -> {
                    String dltTopic = record.topic() + ".DLT";

                    log.error("[KAFKA][ORDER][DLQ][PUBLISHED] topic={} dltTopic={} key={} partition={} offset={} reason={}",
                            record.topic(), dltTopic, record.key(), record.partition(), record.offset(),
                            ex.getClass().getSimpleName(), ex);

                    return new TopicPartition(dltTopic, record.partition());
                }
        );

        // 2초 간격으로 3번 "재시도" 후에도 실패하면 recoverer로 DLT 발행
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(BACKOFF_MS, MAX_RETRY)
        );

        // 재시도해도 성공 못할 케이스는 바로 DLT로 보냄
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);

        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.warn("[KAFKA][ORDER][RETRY][ATTEMPT] topic={} key={} partition={} offset={} attempt={}/3 reason={}",
                    record.topic(), record.key(), record.partition(), record.offset(),
                    deliveryAttempt, ex.getClass().getSimpleName());
        });

        log.info("[KAFKA][ORDER][DLQ][CONFIGURED] backoffMs=2000 maxAttempts=3");

        return errorHandler;
    }
}
