package com.palja.payment_service.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.common.interceptor.KafkaRecordInterceptor;
import com.palja.payment_service.application.event.dto.KafkaEvent;
import com.palja.payment_service.infrastructure.external.kafka.consumer.PaymentSagaFailureRecoverer;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.RecordInterceptor;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

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

    private static final String TRUSTED_BASE_PACKAGE = "com.palja";

    @Bean
    public ConsumerFactory<String, KafkaEvent> sagaConsumerFactory(ObjectMapper objectMapper) {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);

        JsonDeserializer<KafkaEvent> jsonDeserializer =
                new JsonDeserializer<>(KafkaEvent.class, objectMapper, false);
        jsonDeserializer.setUseTypeHeaders(false);
        jsonDeserializer.addTrustedPackages(TRUSTED_BASE_PACKAGE);

        ErrorHandlingDeserializer<KafkaEvent> valueDeserializer =
                new ErrorHandlingDeserializer<>(jsonDeserializer);

        ErrorHandlingDeserializer<String> keyDeserializer =
                new ErrorHandlingDeserializer<>(new StringDeserializer());

        return new DefaultKafkaConsumerFactory<>(props, keyDeserializer, valueDeserializer);
    }

    @Bean(name = "sagaKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, KafkaEvent> sagaKafkaListenerContainerFactory(
            ConsumerFactory<String, KafkaEvent> sagaConsumerFactory,
            CommonErrorHandler sagaErrorHandler,
            RecordInterceptor<String, KafkaEvent> sagaRecordInterceptor
    ) {
        ConcurrentKafkaListenerContainerFactory<String, KafkaEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(sagaConsumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        factory.setRecordInterceptor(sagaRecordInterceptor);
        factory.setCommonErrorHandler(sagaErrorHandler);

        return factory;
    }

    @Bean
    public RecordInterceptor<String, KafkaEvent> sagaRecordInterceptor(Tracer tracer) {
        return new KafkaRecordInterceptor<>(tracer);
    }

    @Bean
    public CommonErrorHandler sagaErrorHandler(PaymentSagaFailureRecoverer recoverer) {
        FixedBackOff backOff = new FixedBackOff(retryIntervalMs, maxRetries);

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);

        handler.addNotRetryableExceptions(
                IllegalArgumentException.class,
                DeserializationException.class
        );

        return handler;
    }
}