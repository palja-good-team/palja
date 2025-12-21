package com.palja.timedeal_service.infrastructure.external.adapter.kafka;

import com.palja.timedeal_service.application.port.ProductEventPublisher;
import com.palja.timedeal_service.application.event.internal.TimeDealStockDecreaseEventReq;
import com.palja.timedeal_service.application.event.internal.TimeDealStockIncreaseEventReq;
import com.palja.timedeal_service.infrastructure.config.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductKafkaProducer implements ProductEventPublisher {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    @Override
    public void publishDecrease(TimeDealStockIncreaseEventReq event) {
        String key = event.getProductId().toString();

        kafkaTemplate.send(
                KafkaTopics.PRODUCT_STOCK_DECREASE,
                key,
                event
        );

        log.info(
                "[Kafka] PRODUCT_STOCK_DECREASE 발행 완료. timeDealId={}, productId={}, quantity={}",
                event.getTimeDealId(),
                event.getProductId(),
                event.getQuantity()
        );
    }

    @Override
    public void publishRestore(TimeDealStockDecreaseEventReq event) {
        String key = event.getProductId().toString();

        kafkaTemplate.send(
                KafkaTopics.PRODUCT_STOCK_RESTORE,
                key,
                event
        );

        log.info(
                "[Kafka] PRODUCT_STOCK_RESTORE 발행 완료. timeDealId={}, productId={}, quantity={}",
                event.getTimeDealId(),
                event.getProductId(),
                event.getQuantity()
        );
    }
}
