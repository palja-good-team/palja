package com.palja.product_service.infrastructure.config;

import com.palja.common.interceptor.KafkaRecordInterceptor;
import com.palja.product_service.application.event.dto.KafkaEvent;
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
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS;

    @Bean
    public KafkaRecordInterceptor<KafkaEvent> consumerInterceptor(Tracer tracer) {

        return new KafkaRecordInterceptor<>(tracer);
    }

    @Bean
    public ConsumerFactory<String, KafkaEvent> consumerFactory() {

        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS,
                        ConsumerConfig.GROUP_ID_CONFIG, "product-service",
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                        JsonDeserializer.TRUSTED_PACKAGES, "*",
                        JsonDeserializer.USE_TYPE_INFO_HEADERS, false,
                        JsonDeserializer.VALUE_DEFAULT_TYPE, KafkaEvent.class
                )
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaEvent> kafkaListenerContainerFactory(Tracer tracer) {
        //빈 이름도 동일하게..
        ConcurrentKafkaListenerContainerFactory<String, KafkaEvent> factory
                = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.setRecordInterceptor(consumerInterceptor(tracer));
        factory.setConcurrency(3);
        factory.getContainerProperties().setPollTimeout(3000);

        return factory;
    }
}
