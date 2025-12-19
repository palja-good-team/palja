package com.palja.product_service.infrastructure.external.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.product_service.application.service.ProductService;
import com.palja.product_service.infrastructure.external.kafka.dto.TimeDealStockDto;
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
    public void handleDecreaseStockEvent(ConsumerRecord<String, Object> dto) {

        String value = (String) dto.value();
        TimeDealStockDto event = test(value, TimeDealStockDto.class);

        productService.decreaseStockForTimeDeal(event.getProductId(), event.getQuantity());
    }

    @KafkaListener(topics = INCREASE_STOCK_TIMEDEAL)
    public void handleIncreaseStockEvent(ConsumerRecord<String, Object> dto) {

        String value = (String) dto.value();
        TimeDealStockDto event = test(value, TimeDealStockDto.class);

        productService.increaseStockForTimeDeal(event.getProductId(), event.getQuantity());
    }

    private <T> T test(Object value, Class<T> type) {

        return objectMapper.convertValue(value, type);
    }
}
