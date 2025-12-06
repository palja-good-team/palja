package com.palja.timedeal_service.application.dto;

import com.palja.timedeal_service.domain.entity.TimeDeal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class TimeDealDetailRes {

    private UUID timeDealId;
    private UUID productId;
    private UUID companyUserId;
    private String title;
    private String description;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private long originalPrice;
    private long timeDealPrice;
    private int discountRate;
    private long totalQuantity;
    private long remainingQuantity;
    private String timeDealStatus;

    public static TimeDealDetailRes from(TimeDeal timeDeal) {
        return TimeDealDetailRes.builder()
                .timeDealId(timeDeal.getTimeDealId())
                .productId(timeDeal.getProductId())
                .companyUserId(timeDeal.getCompanyUserId())
                .title(timeDeal.getTitle())
                .description(timeDeal.getDescription())
                .startAt(timeDeal.getPeriod().getStartAt())
                .endAt(timeDeal.getPeriod().getEndAt())
                .originalPrice(timeDeal.getAmount().getOriginalPrice())
                .timeDealPrice(timeDeal.getAmount().getTimeDealPrice())
                .discountRate(timeDeal.getAmount().getDiscountRate())
                .totalQuantity(timeDeal.getTimeDealStock().getQuantity().getTotalQuantity())
                .remainingQuantity(timeDeal.getTimeDealStock().getQuantity().getRemainingQuantity())
                .timeDealStatus(timeDeal.getTimeDealStatus().name())
                .build();
    }
}
