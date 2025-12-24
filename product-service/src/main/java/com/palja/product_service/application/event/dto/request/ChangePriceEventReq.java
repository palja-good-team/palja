package com.palja.product_service.application.event.dto.request;

import com.palja.product_service.application.event.dto.TimeDealEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangePriceEventReq implements TimeDealEvent {

    private UUID productId;
    private Long price;

    public static ChangePriceEventReq create(UUID productId, Long price) {
        return new ChangePriceEventReq(productId, price);
    }
}
