package com.palja.timedeal_service.application.event.dto;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.palja.timedeal_service.application.event.dto.request.in.TimeDealStockDecreaseEventReq;
import com.palja.timedeal_service.application.event.dto.request.in.TimeDealStockRestoreEventReq;

@JsonSubTypes({
        @JsonSubTypes.Type(value = TimeDealStockDecreaseEventReq.class, name = "StockDecreaseEventReq"),
        @JsonSubTypes.Type(value = TimeDealStockRestoreEventReq.class, name = "StockRestoreEventReq"),
})
public interface OrderEvent extends KafkaEvent{
}
