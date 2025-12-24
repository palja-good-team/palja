package com.palja.product_service.infrastructure.config;

import com.palja.common.interceptor.KafkaProducerInterceptor;
import com.palja.product_service.application.event.dto.ProductEvent;
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

import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS;

    @Bean
    public KafkaProducerInterceptor<ProductEvent> interceptor(Tracer tracer) {
        return new KafkaProducerInterceptor<>(tracer);
    }

    @Bean
    public Map<String, Object> producerConfigs() {

        return Map.of(
                //Producer가 처음으로 연결할 Kafka 브로커의 위치
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class,
                JsonSerializer.ADD_TYPE_INFO_HEADERS, false
        );
    }

    @Bean
    public ProducerFactory<String, ProductEvent> producerFactory() {

        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, ProductEvent> kafkaTemplate(KafkaProducerInterceptor<ProductEvent> interceptor) {

        KafkaTemplate<String, ProductEvent> template = new KafkaTemplate<>(producerFactory());
        template.setProducerInterceptor(interceptor);

        return template;
    }
}
