package com.palja.timedeal_service.application.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.palja.timedeal_service.application.event.dto.request.out.ProductStockDecreaseEventReq;
import com.palja.timedeal_service.application.event.dto.request.out.ProductStockRestoreEventReq;

@JsonSubTypes({
        @JsonSubTypes.Type(value = ProductStockDecreaseEventReq.class, name = "ProductStockDecreaseEventReq"),
        @JsonSubTypes.Type(value = ProductStockRestoreEventReq.class, name = "ProductStockRestoreEventReq"),
})
public interface ProductEvent extends KafkaEvent{
}
