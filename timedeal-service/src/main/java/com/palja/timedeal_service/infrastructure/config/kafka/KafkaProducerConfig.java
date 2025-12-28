package com.palja.timedeal_service.infrastructure.config.kafka;

import com.palja.common.interceptor.KafkaProducerInterceptor;
import com.palja.timedeal_service.application.event.dto.KafkaEvent;
import io.micrometer.tracing.Tracer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

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

        // 타입정보는 "헤더"에 안 넣고, payload(@type)로만 처리
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
            KafkaProducerInterceptor<Object> kafkaProducerInterceptor
    ) {
        KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setProducerInterceptor(kafkaProducerInterceptor);

        return kafkaTemplate;
    }
}
