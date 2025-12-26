package com.palja.product_service.application.event.dto;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.palja.product_service.application.event.dto.request.ChangePriceEventReq;
import com.palja.product_service.application.event.dto.request.DecreaseStockTimeDealErrorEventReq;
import com.palja.product_service.application.event.dto.request.SaleProductErrorEventReq;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,          // 타입 식별자를 "이름"으로
        include = JsonTypeInfo.As.PROPERTY,  // JSON 프로퍼티로 포함
        property = "@type"                   // Type 필드로 구분
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ChangePriceEventReq.class, name = "ChangePriceEventReq"),
        @JsonSubTypes.Type(value = DecreaseStockTimeDealErrorEventReq.class, name = "DecreaseStockTimeDealErrorEventReq"),
        @JsonSubTypes.Type(value = SaleProductErrorEventReq.class, name = "SaleProductErrorEventReq"),
})
public interface ProductEvent{
}
