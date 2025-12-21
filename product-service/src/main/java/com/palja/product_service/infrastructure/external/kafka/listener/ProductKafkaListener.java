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
    public void handleDecreaseStockTimeDealEvent(StockDecreaseTimeDealDto dto) {

        productService.decreaseStockForTimeDeal(dto.getProductId(), dto.getQuantity());
    }

    @KafkaListener(topics = SALE_STOCK_ORDER)
    public void handleDecreaseStockOrderEvent(StockDecreaseOrderEventDto dto) {

        if (dto.getIsTimeDeal().equals(Boolean.FALSE)) {
            productService.saleProduct(
                    dto.getSagaId(), dto.getProductId(), dto.getOrderId(), dto.getQuantity());
        }
    }

    @KafkaListener(topics = {INCREASE_STOCK_TIMEDEAL, RESTORE_STOCK_ORDER, ORDER_CANCEL})
    public void handleIncreaseStockEvent(StockRestoreEventDto dto) {

        if(dto.getIsTimeDeal() == null || dto.getIsTimeDeal().equals(Boolean.FALSE)) {
            productService.stockRestore(dto.getProductId(), dto.getQuantity());
        }
    }
}
