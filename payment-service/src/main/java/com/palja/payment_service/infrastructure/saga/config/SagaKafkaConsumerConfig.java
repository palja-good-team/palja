package com.palja.payment_service.infrastructure.saga.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.payment_service.infrastructure.saga.listener.PaymentSagaFailureRecoverer;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class SagaKafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${payment.saga.consumer.group-id}")
    private String groupId;

    @Value("${payment.saga.consumer.auto-offset-reset:earliest}")
    private String autoOffsetReset;

    @Value("${payment.saga.consumer.max-retries:3}")
    private long maxRetries;

    @Value("${payment.saga.consumer.retry-interval-ms:1000}")
    private long retryIntervalMs;

    private static final String TRUSTED_PACKAGES = "com.palja.*";

    @Bean
    public ConsumerFactory<String, Object> sagaConsumerFactory(ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        JsonDeserializer<Object> valueDeserializer = new JsonDeserializer<>(Object.class, objectMapper, false);
        valueDeserializer.addTrustedPackages(TRUSTED_PACKAGES);
        valueDeserializer.setUseTypeHeaders(false);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> sagaKafkaListenerContainerFactory(
            ConsumerFactory<String, Object> sagaConsumerFactory,
            DefaultErrorHandler sagaErrorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sagaConsumerFactory);

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        factory.setCommonErrorHandler(sagaErrorHandler);
        return factory;
    }

    /*
     재시도 후에도 실패하면 Recoverer에서 failure 응답 발행 + commit 처리
     */
    @Bean
    public DefaultErrorHandler sagaErrorHandler(PaymentSagaFailureRecoverer recoverer) {
        FixedBackOff backOff = new FixedBackOff(retryIntervalMs, maxRetries);

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);

        handler.addNotRetryableExceptions(
                IllegalArgumentException.class,
                org.springframework.kafka.support.serializer.DeserializationException.class
        );

        return handler;
    }

    @Bean
    public RecordInterceptor<String, Object> sagaRecordInterceptor(Tracer tracer) {
        return (record, consumer) -> record;
    }
}
