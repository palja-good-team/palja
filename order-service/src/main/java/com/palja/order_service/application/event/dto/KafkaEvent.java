package com.palja.order_service.application.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,          // 타입 식별자를 "이름"으로
        include = JsonTypeInfo.As.PROPERTY,  // JSON 프로퍼티로 포함
        property = "@type"                   // Type 필드로 구분
)
@JsonSubTypes({
        // 필요한 이벤트 계속 추가
        @JsonSubTypes.Type(value = CouponEvent.class, name = "CouponEvent"),
        @JsonSubTypes.Type(value = OrderEvent.class, name = "OrderEvent"),
        @JsonSubTypes.Type(value = PaymentEvent.class, name = "PaymentEvent"),
        @JsonSubTypes.Type(value = ProductEvent.class, name = "ProductEvent"),
        @JsonSubTypes.Type(value = SagaEvent.class, name = "SagaEvent"),
        @JsonSubTypes.Type(value = TimeDealEvent.class, name = "TimeDealEvent"),
})
public interface KafkaEvent {
}