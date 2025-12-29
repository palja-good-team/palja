package com.palja.timedeal_service.infrastructure.config.kafka;

import com.palja.common.interceptor.KafkaRecordInterceptor;
import com.palja.timedeal_service.application.event.dto.KafkaEvent;
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
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    // consumer 설정
    @Bean
    public KafkaRecordInterceptor<KafkaEvent> consumerInterceptor(Tracer tracer) {
        return new KafkaRecordInterceptor<>(tracer);
    }

    @Bean
    public ConsumerFactory<String, KafkaEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

        // 에러 핸들링 가능한 역직렬화
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        // 헤더 타입 정보 대신 payload의 @type만 사용
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        // 기본 타입을 KafkaEvent로 고정
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, KafkaEvent.class);

        // 보안
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.palja.*");

        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaEvent> kafkaListenerContainerFactory(
            KafkaRecordInterceptor<KafkaEvent> consumerInterceptor,
            ConsumerFactory<String, KafkaEvent> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, KafkaEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setRecordInterceptor(consumerInterceptor);
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(1);

        return factory;
    }
}
