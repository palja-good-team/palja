package com.palja.product_service.infrastructure.external.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.product_service.application.service.ProductService;
import com.palja.product_service.infrastructure.external.kafka.dto.StockDecreaseOrderEventDto;
import com.palja.product_service.infrastructure.external.kafka.dto.StockDecreaseTimeDealDto;
import com.palja.product_service.infrastructure.external.kafka.dto.StockRestoreEventDto;
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

    @KafkaListener(topics = DECREASE_STOCK_TIMEDEAL)
    public void handleDecreaseStockTimeDealEvent(ConsumerRecord<String, Object> dto) {

        String value = (String) dto.value();
        StockDecreaseTimeDealDto event = Deserialization(value, StockDecreaseTimeDealDto.class);

    }
    @KafkaListener(topics = SALE_STOCK_ORDER)
    public void handleDecreaseStockOrderEvent(ConsumerRecord<String, Object> dto) {

        String value = (String) dto.value();
        StockDecreaseOrderEventDto event = Deserialization(value, StockDecreaseOrderEventDto.class);

        if (event.getIsTimeDeal().equals(Boolean.FALSE)) {
            productService.saleProduct(event.getSagaId(), event.getProductId(), event.getQuantity());
        }
    }

    @KafkaListener(topics = {INCREASE_STOCK_TIMEDEAL, RESTORE_STOCK_ORDER, ORDER_CANCEL})
    public void handleIncreaseStockEvent(ConsumerRecord<String, Object> dto) {

        String value = (String) dto.value();
        StockRestoreEventDto event = Deserialization(value, StockRestoreEventDto.class);

        if(event.getIsTimeDeal() == null || event.getIsTimeDeal().equals(false)) {
            productService.stockRestore(event.getProductId(), event.getQuantity());
        }
    }

    private <T> T Deserialization(Object value, Class<T> type) {

        return objectMapper.convertValue(value, type);
    }
}
