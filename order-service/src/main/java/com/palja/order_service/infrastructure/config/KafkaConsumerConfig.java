package com.palja.order_service.infrastructure.config;

import com.palja.common.interceptor.KafkaRecordInterceptor;
import com.palja.order_service.application.event.dto.KafkaEvent;
import io.micrometer.tracing.Tracer;
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
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Consumer 설정
 */
@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:order-service}")
    private String groupId;

    @Bean
    public KafkaRecordInterceptor<KafkaEvent> consumerInterceptor(Tracer tracer) {
        return new KafkaRecordInterceptor<>(tracer);
    }

    @Bean
    public ConsumerFactory<String, KafkaEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // Deserializer (에러 핸들링 포함)
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        // JSON (payload의 "@type"으로 다형성 처리)
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.palja.*");
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        // KafkaEvent 인터페이스 기준으로 역직렬화 (Jackson이 @type 보고 서브타입 선택)
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, KafkaEvent.class);

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, KafkaEvent> consumerFactory,
            KafkaRecordInterceptor<KafkaEvent> consumerInterceptor,
            CommonErrorHandler errorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<String, KafkaEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setRecordInterceptor(consumerInterceptor);  // Interceptor 적용
        factory.setCommonErrorHandler(errorHandler);        // Error Handler 적용

        return factory;
    }
}