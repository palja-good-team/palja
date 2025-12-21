package com.palja.product_service.infrastructure.config;

import com.palja.common.interceptor.KafkaRecordInterceptor;
import com.palja.product_service.infrastructure.external.kafka.dto.StockDecreaseOrderEventDto;
import com.palja.product_service.infrastructure.external.kafka.dto.StockDecreaseTimeDealDto;
import com.palja.product_service.infrastructure.external.kafka.dto.StockRestoreEventDto;
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
import org.springframework.kafka.support.serializer.DelegatingByTopicDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;
import java.util.regex.Pattern;

import static com.palja.product_service.infrastructure.external.kafka.ProductKafkaTopic.*;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS;

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
//                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
//                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
                        JsonDeserializer.TRUSTED_PACKAGES, "*"
                ), new StringDeserializer(),
                new DelegatingByTopicDeserializer(Map.of(
                        Pattern.compile(DECREASE_STOCK_TIMEDEAL),
                        new JsonDeserializer<>(StockDecreaseTimeDealDto.class, false),
                        Pattern.compile(SALE_STOCK_ORDER),
                        new JsonDeserializer<>(StockDecreaseOrderEventDto.class, false),
                        Pattern.compile(".*restore.*"),
                        new JsonDeserializer<>(StockRestoreEventDto.class, false),
                        Pattern.compile(ORDER_CANCEL),
                        new JsonDeserializer<>(StockRestoreEventDto.class, false)
                ), new JsonDeserializer<Object>())
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
}
