package com.palja.timedeal_service.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    // 발행용 TOPIC
    @Bean
    public NewTopic stockDecreaseTopic() {
        return TopicBuilder.name(KafkaTopics.PRODUCT_STOCK_DECREASE)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic stockRestoreTopic() {
        return TopicBuilder.name(KafkaTopics.PRODUCT_STOCK_RESTORE)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
