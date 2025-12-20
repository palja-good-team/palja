package com.palja.product_service.infrastructure.external.kafka.producer;

import com.palja.product_service.application.event.ChangePriceEvent;
import com.palja.product_service.application.event.DecreaseStockTimeDealErrorEvent;
import com.palja.product_service.application.event.SaleProductErrorEvent;
import com.palja.product_service.application.event.publisher.ProductEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static com.palja.product_service.infrastructure.external.kafka.ProductKafkaTopic.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductKafkaProducer implements ProductEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private void publish(String topic, Object productEvent) {

        CompletableFuture<SendResult<String, Object>> send = kafkaTemplate.send(topic, productEvent);
        send.whenComplete((result, exception) -> {

            if(result != null) {
                log.info("{}에 대한 메세지 전송 성공", result.getRecordMetadata().topic());
            }
            else {
                log.error("{}에 대한 메세지 전송 실패 - 이유 = {}", topic, exception.getMessage());
            }
        });
    }

    @Override
    public void handleChangePriceEvent(ChangePriceEvent event) {

        publish(CHANGE_PRODUCT_PRICE, event);
    }

    @Override
    public void handleSaleProductErrorEvent(SaleProductErrorEvent event) {

        publish(SALE_STOCK_ORDER, event);
    }

    @Override
    public void handleDecreaseStockTimeDealErrorEvent(DecreaseStockTimeDealErrorEvent event) {

        publish(DECREASE_STOCK_TIMEDEAL_ERROR, event);
    }
}
