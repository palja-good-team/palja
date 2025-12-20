package com.palja.product_service.infrastructure.config;

import com.palja.common.interceptor.KafkaProducerInterceptor;
import com.palja.common.interceptor.KafkaRecordInterceptor;
import io.micrometer.tracing.Tracer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

import static com.palja.product_service.infrastructure.external.kafka.ProductKafkaTopic.CHANGE_PRODUCT_PRICE;
import static com.palja.product_service.infrastructure.external.kafka.ProductKafkaTopic.DECREASE_STOCK_TIMEDEAL;
import static org.springframework.kafka.core.KafkaAdmin.NewTopics;

@EnableKafka
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS;

    @Bean
    public NewTopics TimeDealConsumer() {

        return new NewTopics(
                TopicBuilder.name(CHANGE_PRODUCT_PRICE).build(),
                TopicBuilder.name(DECREASE_STOCK_TIMEDEAL).build()
        );
    }

    @Bean
    public KafkaProducerInterceptor<Object> interceptor(Tracer tracer) {
        return new KafkaProducerInterceptor<>(tracer);
    }

    @Bean
    public Map<String, Object> producerConfigs() {

        return Map.of(
                //Producer가 처음으로 연결할 Kafka 브로커의 위치
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {

        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaRecordInterceptor<Object> consumerInterceptor(Tracer tracer) {

        return new KafkaRecordInterceptor<>(tracer);
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {

        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS,
                        ConsumerConfig.GROUP_ID_CONFIG, "product-service",
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class
                )
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(Tracer tracer) {
        //빈 이름도 동일하게..
        ConcurrentKafkaListenerContainerFactory<String, Object> factory
                = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.setRecordInterceptor(consumerInterceptor(tracer));
        factory.setConcurrency(3);
        factory.getContainerProperties().setPollTimeout(3000);

        return factory;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(KafkaProducerInterceptor<Object> interceptor) {

        KafkaTemplate<String, Object> template = new KafkaTemplate<>(producerFactory());
        template.setProducerInterceptor(interceptor);

        return template;
    }
}
