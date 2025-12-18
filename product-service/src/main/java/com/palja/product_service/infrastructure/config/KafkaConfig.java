package com.palja.product_service.infrastructure.config;

import com.palja.common.interceptor.KafkaProducerInterceptor;
import com.palja.product_service.application.event.ProductEvent;
import com.palja.product_service.infrastructure.external.kafka.ProductKafkaTopic;
import io.micrometer.tracing.Tracer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

import static org.springframework.kafka.core.KafkaAdmin.NewTopics;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopics TimeDealConsumer() {

        return new NewTopics(
                TopicBuilder.name(ProductKafkaTopic.CHANGE_PRODUCT_PRICE.getTopicName())
                        .build()
        );
    }

    @Bean
    public KafkaProducerInterceptor<ProductEvent> interceptor(Tracer tracer) {
        return new KafkaProducerInterceptor<>(tracer);
    }

    @Bean
    public Map<String, Object> producerConfigs() {

        return Map.of(
                //Producer가 처음으로 연결할 Kafka 브로커의 위치
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                //Kafka는 byte Array로 변환하기 때문에 레디스와 다르게 String을 넣어도 객체를 잘 처리함
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
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
