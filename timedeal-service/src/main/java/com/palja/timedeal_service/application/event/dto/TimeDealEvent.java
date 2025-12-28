package com.palja.timedeal_service.application.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.palja.timedeal_service.application.event.dto.request.out.ProductStockDecreaseEventReq;
import com.palja.timedeal_service.application.event.dto.request.out.ProductStockRestoreEventReq;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,          // 타입 식별자를 "이름"으로
        include = JsonTypeInfo.As.PROPERTY,  // JSON 프로퍼티로 포함
        property = "@type"                   // Type 필드로 구분
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ProductStockDecreaseEventReq.class, name = "ProductStockDecreaseEventReq"),
        @JsonSubTypes.Type(value = ProductStockRestoreEventReq.class, name = "ProductStockRestoreEventReq")
})
public interface TimeDealEvent {
}
