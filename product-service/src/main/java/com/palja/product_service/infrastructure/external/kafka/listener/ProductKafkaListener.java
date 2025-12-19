package com.palja.product_service.infrastructure.external.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.product_service.application.service.ProductService;
import com.palja.product_service.infrastructure.external.kafka.dto.StockDecreaseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.palja.product_service.infrastructure.external.kafka.ProductKafkaTopic.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductKafkaListener {

    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = {DECREASE_STOCK_TIMEDEAL, DECREASE_STOCK_ORDER})
    public void handleDecreaseStockEvent(ConsumerRecord<String, Object> dto) {

        String value = (String) dto.value();
        StockDecreaseDto event = test(value, StockDecreaseDto.class);

        // 타임딜이건 주문이건, 상품은 팔렸다고 가정.
        if(event.getIsTimeDeal() == null || event.getIsTimeDeal().equals(false)) {
            productService.saleProductV1(event.getProductId(), event.getQuantity());
//            productService.saleProduct(event.getProductId(), event.getQuantity());
        }
    }

    @KafkaListener(topics = {INCREASE_STOCK_TIMEDEAL, RESTORE_STOCK_ORDER, ORDER_CANCEL})
    public void handleIncreaseStockEvent(ConsumerRecord<String, Object> dto) {

        String value = (String) dto.value();
        StockDecreaseDto event = test(value, StockDecreaseDto.class);


        if(event.getIsTimeDeal() == null || event.getIsTimeDeal().equals(false)) {
            productService.stockRestoreV1(event.getProductId(), event.getQuantity());
//            productService.stockRestore(event.getProductId(), event.getQuantity());
        }
    }

    private <T> T test(Object value, Class<T> type) {

        return objectMapper.convertValue(value, type);
    }
}
