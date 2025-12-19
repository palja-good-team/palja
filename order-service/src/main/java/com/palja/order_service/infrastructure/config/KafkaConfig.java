package com.palja.order_service.infrastructure.config;

import com.palja.common.interceptor.KafkaProducerInterceptor;
import com.palja.common.interceptor.KafkaRecordInterceptor;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka 설정 (Producer + Consumer + DLQ)
 */
@Slf4j
@EnableKafka
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:order-service}")
    private String groupId;

    // ===== Producer =====
    @Bean
    public KafkaProducerInterceptor<Object> kafkaEventProducerInterceptor(Tracer tracer) {
        return new KafkaProducerInterceptor<>(tracer);
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // 멱등성 보장
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(KafkaProducerInterceptor<Object> kafkaProducerInterceptor) {
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setProducerInterceptor(kafkaProducerInterceptor);

        return kafkaTemplate;
    }

    // ===== Consumer =====
    @Bean
    public KafkaRecordInterceptor<Object> consumerInterceptor(Tracer tracer) {
        return new KafkaRecordInterceptor<>(tracer);
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // Deserializer (에러 핸들링 포함)
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        // JSON 설정
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.palja.*");

        return new DefaultKafkaConsumerFactory<>(config);
    }

    // === DLQ Error Handler ===
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

                    return new org.apache.kafka.common.TopicPartition(dltTopic, record.partition());
                }
        );

        // 재시도 전략: 2초 간격으로 3번 재시도
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(2000L, 3L)  // 2초 간격, 3회 재시도
        );

        // 재시도하지 않을 예외 정의 (즉시 DLT로)
        errorHandler.addNotRetryableExceptions(
                IllegalArgumentException.class  // 잘못된 인자 → 재시도 무의미
        );

        // 재시도 로깅
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.warn("[KAFKA][ORDER][RETRY][ATTEMPT] topic={} key={} partition={} offset={} attempt={}/3 reason={}",
                    record.topic(), record.key(), record.partition(), record.offset(),
                    deliveryAttempt, ex.getClass().getSimpleName());
        });

        log.info("[KAFKA][ORDER][DLQ][CONFIGURED] backoffMs=2000 maxAttempts=3");

        return errorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            KafkaRecordInterceptor<Object> consumerInterceptor,
            CommonErrorHandler errorHandler
    ) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.setRecordInterceptor(consumerInterceptor);   // Interceptor 적용
        factory.setCommonErrorHandler(errorHandler);  // Error Handler 적용

        return factory;
    }
}