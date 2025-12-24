package com.palja.product_service.infrastructure.external.kafka.consumer;

import com.palja.product_service.application.service.ProductService;
import com.palja.product_service.infrastructure.external.kafka.dto.StockDecreaseOrderDto;
import com.palja.product_service.infrastructure.external.kafka.dto.StockDecreaseTimeDealDto;
import com.palja.product_service.infrastructure.external.kafka.dto.StockRestoreOrderEventDto;
import com.palja.product_service.infrastructure.external.kafka.dto.StockRestoreTimeDealEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.palja.product_service.infrastructure.external.kafka.ProductKafkaTopic.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductKafkaConsumer {

    private final ProductService productService;

    @KafkaListener(topics = DECREASE_STOCK_TIMEDEAL)
    public void handleDecreaseStockTimeDealEvent(StockDecreaseTimeDealDto dto) {

        productService.decreaseStockForTimeDeal(dto.getProductId(), dto.getQuantity());
    }

    @KafkaListener(topics = SALE_STOCK_ORDER)
    public void handleDecreaseStockOrderEvent(StockDecreaseOrderDto dto) {

        if (dto.getIsTimeDeal().equals(Boolean.FALSE)) {
            productService.saleProduct(
                    dto.getSagaId(), dto.getProductId(), dto.getOrderId(), dto.getQuantity());
        }
    }

    @KafkaListener(topics = INCREASE_STOCK_TIMEDEAL)
    public void handleIncreaseStockTimeDealEvent(StockRestoreTimeDealEventDto dto) {

        productService.stockRestore(dto.getProductId(), dto.getQuantity());
    }

    @KafkaListener(topics = {RESTORE_STOCK_ORDER, ORDER_CANCEL})
    public void handleIncreaseStockEvent(StockRestoreOrderEventDto dto) {

        productService.stockRestore(dto.getProductId(), dto.getQuantity());
    }
}
