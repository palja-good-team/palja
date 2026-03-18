package com.palja.product_service.infrastructure.external.kafka.producer;

import com.palja.product_service.application.event.dto.ProductEvent;
import com.palja.product_service.application.event.dto.request.ChangePriceEventReq;
import com.palja.product_service.application.event.dto.request.DecreaseStockTimeDealErrorEventReq;
import com.palja.product_service.application.event.dto.request.SaleProductErrorEventReq;
import com.palja.product_service.application.port.ProductEventPublisher;
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

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    private void publish(String topic, ProductEvent productEvent) {

        CompletableFuture<SendResult<String, ProductEvent>> send = kafkaTemplate.send(topic, productEvent);
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
    public void handleChangePriceEvent(ChangePriceEventReq event) {

        publish(CHANGE_PRODUCT_PRICE, event);
    }

    @Override
    public void handleSaleProductErrorEvent(SaleProductErrorEventReq event) {

        publish(SALE_STOCK_ORDER_ERROR, event);
    }

    @Override
    public void handleDecreaseStockTimeDealErrorEvent(DecreaseStockTimeDealErrorEventReq event) {

        publish(DECREASE_STOCK_TIMEDEAL_ERROR, event);
    }
}
