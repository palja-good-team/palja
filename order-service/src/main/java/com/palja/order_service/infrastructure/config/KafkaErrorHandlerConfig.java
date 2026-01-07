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
 * Kafka Error Handler 설정
 * - 재시도(backoff) 후에도 실패하면 <원본토픽>.DLT 로 전송
 */
@Slf4j
@Configuration
public class KafkaErrorHandlerConfig {

    private static final long BACKOFF_MS = 2_000L;
    private static final long MAX_RETRY = 3L; // 추가 재시도 횟수 (총 시도 = 1 + MAX_RETRY)

    @Bean
    public CommonErrorHandler kafkaErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        long maxAttempts = 1 + MAX_RETRY;

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> {
                    String originalTopic = record.topic();
                    // DLT 토픽 이름 규칙: 원본토픽.DLT
                    String dltTopic = originalTopic + ".DLT";

                    // DLT 전송은 장애 상황: 운영 감시용 ERROR 로그
                    log.error("DLT 전송 라우팅 (redirect to DLT): originalTopic={} dltTopic={} key={} partition={} offset={} errorType={}",
                            originalTopic, dltTopic, record.key(), record.partition(), record.offset(),
                            ex.getClass().getSimpleName(), ex);

                    // 파티션 유지(순서/분산 고려) - 기존 전략 유지
                    return new TopicPartition(dltTopic, record.partition());
                }
        );

        // 재시도 후에도 실패하면 recoverer로 DLT 발행
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(BACKOFF_MS, MAX_RETRY)
        );

        // 재시도 무의미한 예외는 즉시 DLT로 (필요 시 계속 추가)
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);

        // 재시도 로그는 WARN: 시도 횟수/원인만 기록 로그
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.warn("Kafka 재시도 (retry): topic={} key={} partition={} offset={} attempt={}/{} errorType={}",
                    record.topic(), record.key(), record.partition(), record.offset(),
                    deliveryAttempt, maxAttempts, ex.getClass().getSimpleName());
        });

        // 부팅 시 1회: 운영에서 재시도/DTL 정책 확인용 로그
        log.info("Kafka 에러 핸들러 설정 완료 (error handler configured): backoffMs={} maxRetry={} maxAttempts={}",
                BACKOFF_MS, MAX_RETRY, maxAttempts);

        return errorHandler;
    }
}