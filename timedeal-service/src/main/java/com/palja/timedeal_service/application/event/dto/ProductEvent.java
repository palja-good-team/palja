package com.palja.timedeal_service.application.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.palja.timedeal_service.application.event.dto.request.in.ProductPriceUpdateEventReq;
import com.palja.timedeal_service.application.event.dto.request.in.ProductStockDecreaseFailureEventReq;

@JsonSubTypes({
        @JsonSubTypes.Type(value = ProductStockDecreaseFailureEventReq.class, name = "DecreaseStockTimeDealErrorEventReq"),
        @JsonSubTypes.Type(value = ProductPriceUpdateEventReq.class, name = "ChangePriceEventReq"),
})
public interface ProductEvent extends KafkaEvent{
}
