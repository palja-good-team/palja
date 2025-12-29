package com.palja.coupon_service.application.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.palja.coupon_service.application.event.dto.request.CouponCancelEventReq;
import com.palja.coupon_service.application.event.dto.request.CouponUseEventReq;
import com.palja.coupon_service.application.event.dto.response.CouponCancelEventRes;
import com.palja.coupon_service.application.event.dto.response.CouponUseEventRse;

@JsonSubTypes({
        @JsonSubTypes.Type(value = CouponCancelEventReq.class, name = "CouponCancelEventReq"),
        @JsonSubTypes.Type(value = CouponUseEventReq.class, name = "CouponUseEventReq"),

        @JsonSubTypes.Type(value = CouponCancelEventRes.class, name = "CouponCancelEventRes"),
        @JsonSubTypes.Type(value = CouponUseEventRse.class, name = "SagaStepEventRes"),
})
public interface OrderEvent extends KafkaEvent {
}
