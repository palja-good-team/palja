package com.palja.order_service.infrastructure.external.client.dto.response;

import com.palja.order_service.application.dto.external.TimeDealRes;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private BigDecimal originalPrice;
    private BigDecimal timeDealPrice;
    private int discountRate;
    private Long totalQuantity;
    private Long remainingQuantity;
    private String timeDealStatus;  // ENUM(PENDING, OPEN, SOLD_OUT, CLOSED)

    public TimeDealRes toResponse() {
        return TimeDealRes.builder()
                .timeDealId(timeDealId)
                .productId(productId)
                .startAt(startAt)
                .endAt(endAt)
                .timeDealPrice(timeDealPrice)
                .discountRate(discountRate)
                .timeDealStockQuantity(remainingQuantity)
                .status(timeDealStatus)
                .build();
    }
}