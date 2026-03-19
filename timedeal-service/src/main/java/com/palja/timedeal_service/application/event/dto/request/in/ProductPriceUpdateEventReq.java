package com.palja.timedeal_service.application.event.dto.request.in;

import com.palja.timedeal_service.application.event.dto.ProductEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProductPriceUpdateEventReq implements ProductEvent {

    private UUID productId;
    private Long price;
}
