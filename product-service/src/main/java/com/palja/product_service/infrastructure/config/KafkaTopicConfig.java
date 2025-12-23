package com.palja.product_service.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import static com.palja.product_service.infrastructure.external.kafka.ProductKafkaTopic.*;
import static com.palja.product_service.infrastructure.external.kafka.ProductKafkaTopic.CHANGE_PRODUCT_PRICE;
import static com.palja.product_service.infrastructure.external.kafka.ProductKafkaTopic.ORDER_CANCEL;
import static com.palja.product_service.infrastructure.external.kafka.ProductKafkaTopic.RESTORE_STOCK_ORDER;
import static com.palja.product_service.infrastructure.external.kafka.ProductKafkaTopic.SALE_STOCK_ORDER;
import static com.palja.product_service.infrastructure.external.kafka.ProductKafkaTopic.SALE_STOCK_ORDER_ERROR;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public KafkaAdmin.NewTopics TimeDealConsumer() {

        return new KafkaAdmin.NewTopics(
                TopicBuilder.name(DECREASE_STOCK_TIMEDEAL).partitions(3).replicas(1).build(),
                TopicBuilder.name(DECREASE_STOCK_TIMEDEAL_ERROR).partitions(3).replicas(1).build(),
                TopicBuilder.name(INCREASE_STOCK_TIMEDEAL).partitions(3).replicas(1).build(),
                TopicBuilder.name(CHANGE_PRODUCT_PRICE).partitions(3).replicas(1).build(),
                TopicBuilder.name(SALE_STOCK_ORDER).partitions(3).replicas(1).build(),
                TopicBuilder.name(SALE_STOCK_ORDER_ERROR).partitions(3).replicas(1).build(),
                TopicBuilder.name(RESTORE_STOCK_ORDER).partitions(3).replicas(1).build(),
                TopicBuilder.name(ORDER_CANCEL).partitions(3).replicas(1).build()
        );
    }
}
