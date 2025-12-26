package com.palja.product_service.application.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,          // 타입 식별자를 "이름"으로
        include = JsonTypeInfo.As.PROPERTY,  // JSON 프로퍼티로 포함
        property = "@type"                   // Type 필드로 구분
)
@JsonSubTypes({
        // 필요한 이벤트 계속 추가
        @JsonSubTypes.Type(value = TimeDealEvent.class, name = "TimeDealEvent"),
        @JsonSubTypes.Type(value = OrderEvent.class, name = "OrderEvent"),
        @JsonSubTypes.Type(value = ProductEvent.class, name = "ProductEvent"),
})
public interface KafkaEvent {
}
