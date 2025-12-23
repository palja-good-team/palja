package com.palja.order_service.application.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.palja.order_service.application.event.dto.request.*;
import com.palja.order_service.application.event.dto.response.PaymentCreateEventRes;
import com.palja.order_service.application.event.dto.response.SagaStepEventRes;

@JsonSubTypes({
        @JsonSubTypes.Type(value = StockDecreaseEventReq.class, name = "StockDecreaseEventReq"),
        @JsonSubTypes.Type(value = StockRestoreEventReq.class, name = "StockRestoreEventReq"),
        @JsonSubTypes.Type(value = CouponUseEventReq.class, name = "CouponUseEventReq"),
        @JsonSubTypes.Type(value = CouponCancelEventReq.class, name = "CouponCancelEventReq"),
        @JsonSubTypes.Type(value = PaymentCreateEventReq.class, name = "PaymentCreateEventReq"),
        @JsonSubTypes.Type(value = PaymentCancelEventReq.class, name = "PaymentCancelEventReq"),

        @JsonSubTypes.Type(value = SagaStepEventRes.class, name = "SagaStepEventRes"),
        @JsonSubTypes.Type(value = PaymentCreateEventRes.class, name = "PaymentCreateEventRes"),
})
public interface SagaEvent extends KafkaEvent {
}