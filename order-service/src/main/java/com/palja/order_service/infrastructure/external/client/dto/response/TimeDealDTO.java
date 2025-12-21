package com.palja.order_service.infrastructure.external.client.dto.response;

import com.palja.order_service.application.dto.external.TimeDealRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeDealDTO {

    private UUID timeDealId;
    private UUID productId;
    private UUID companyUserId;
    private String title;
    private String description;

    private TimeDealPeriodDTO period;
    private TimeDealAmountDTO amount;
    private TimeDealQuantityDTO quantity;

    private String timeDealStatus; // ENUM(PENDING, OPEN, SOLD_OUT, CLOSED)

    public TimeDealRes toResponse() {
        return TimeDealRes.builder()
                .timeDealId(timeDealId)
                .productId(productId)
                .startAt(period != null ? period.getStartAt() : null)
                .endAt(period != null ? period.getEndAt() : null)
                .timeDealPrice(amount != null ? amount.getTimeDealPrice() : null)
                .discountRate(amount != null ? amount.getDiscountRate() : 0)
                .timeDealStockQuantity(quantity != null ? quantity.getRemainingQuantity() : null)
                .status(timeDealStatus)
                .build();
    }
}