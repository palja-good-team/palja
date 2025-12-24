package com.palja.product_service.application.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.palja.product_service.infrastructure.external.kafka.dto.StockDecreaseOrderDto;
import com.palja.product_service.infrastructure.external.kafka.dto.StockRestoreOrderEventDto;

@JsonSubTypes({
        @JsonSubTypes.Type(value = StockDecreaseOrderDto.class, name = "StockDecreaseOrderDto"),
        @JsonSubTypes.Type(value = StockRestoreOrderEventDto.class, name = "StockRestoreOrderEventDto")
})
public interface OrderEvent extends KafkaEvent{
}
