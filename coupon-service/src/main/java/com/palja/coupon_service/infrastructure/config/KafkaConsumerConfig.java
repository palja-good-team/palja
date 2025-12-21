package com.palja.coupon_service.infrastructure.config;

import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import com.palja.common.interceptor.KafkaRecordInterceptor;
import org.apache.kafka.common.serialization.StringDeserializer;
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
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, Object.class.getName());
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            KafkaRecordInterceptor<Object> consumerInterceptor,
            CommonErrorHandler errorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setRecordInterceptor(consumerInterceptor);  // Interceptor 적용
        factory.setCommonErrorHandler(errorHandler);        // Error Handler 적용
        return factory;
    }

    @Bean
    public KafkaRecordInterceptor<Object> consumerInterceptor(Tracer tracer) {
        return new KafkaRecordInterceptor<>(tracer);
    }

    @Bean
    public CommonErrorHandler kafkaErrorHandler(KafkaTemplate<String, Object> kafkaTemplate) {

        // DLT(Dead Letter Topic)로 메시지 전송
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                // DLT 토픽 이름 규칙: 원본토픽.DLT
                (record, ex) -> {
                    String dltTopic = record.topic() + ".DLT";

                    log.error("[KAFKA][COUPON][DLQ][PUBLISHED] topic={} dltTopic={} key={} partition={} offset={} reason={}",
                            record.topic(), dltTopic, record.key(), record.partition(), record.offset(),
                            ex.getClass().getSimpleName(), ex);

                    return new org.apache.kafka.common.TopicPartition(dltTopic, record.partition());
                }
        );

        // 재시도 전략: 3초 간격으로 3번 재시도
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(3000L, 3L)  // 3초 간격, 3회 재시도
        );

        // 재시도하지 않을 예외 정의 (즉시 DLT로)
        errorHandler.addNotRetryableExceptions(
                IllegalArgumentException.class  // 잘못된 인자 → 재시도 무의미
        );

        // 재시도 로깅
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.warn("[KAFKA][COUPON][RETRY][ATTEMPT] topic={} key={} partition={} offset={} attempt={}/3 reason={}",
                    record.topic(), record.key(), record.partition(), record.offset(),
                    deliveryAttempt, ex.getClass().getSimpleName());
        });

        log.info("[KAFKA][COUPON][DLQ][CONFIGURED] backoffMs=2000 maxAttempts=3");

        return errorHandler;
    }
}
