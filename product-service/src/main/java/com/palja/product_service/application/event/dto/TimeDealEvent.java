package com.palja.product_service.application.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.palja.product_service.infrastructure.external.kafka.dto.StockDecreaseTimeDealDto;
import com.palja.product_service.infrastructure.external.kafka.dto.StockRestoreTimeDealEventDto;

@JsonSubTypes({
        @JsonSubTypes.Type(value = StockDecreaseTimeDealDto.class, name = "ProductStockDecreaseEventReq"),
        @JsonSubTypes.Type(value = StockRestoreTimeDealEventDto.class, name = "ProductStockRestoreEventReq"),
})
public interface TimeDealEvent extends KafkaEvent {
}
