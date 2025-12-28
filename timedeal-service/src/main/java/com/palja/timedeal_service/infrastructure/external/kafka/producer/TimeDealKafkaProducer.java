package com.palja.timedeal_service.infrastructure.external.kafka.producer;

import com.palja.timedeal_service.application.event.dto.TimeDealEvent;
import com.palja.timedeal_service.application.port.TimeDealEventPublisher;
import com.palja.timedeal_service.application.event.dto.request.out.ProductStockRestoreEventReq;
import com.palja.timedeal_service.application.event.dto.request.out.ProductStockDecreaseEventReq;
import com.palja.timedeal_service.infrastructure.external.kafka.topic.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeDealKafkaProducer implements TimeDealEventPublisher {

    private final KafkaTemplate<String, TimeDealEvent> kafkaTemplate;

    @Override
    public void publishProductStockDecrease(ProductStockDecreaseEventReq event) {
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
    public void publishProductStockRestore(ProductStockRestoreEventReq event) {
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
