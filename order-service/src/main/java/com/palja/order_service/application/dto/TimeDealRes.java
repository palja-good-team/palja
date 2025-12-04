package com.palja.order_service.application.dto;

import com.palja.order_service.infrastructure.external.dto.response.TimeDealDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeDealRes {

    private final UUID timeDealId;
    private final UUID productId;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final BigDecimal timeDealPrice;
    private final int discountRate;
    private final int timeDealStockQuantity;
    private final String status;

    // Infrastructure DTO → Application DTO 변환
    public static TimeDealRes from(TimeDealDTO timeDealDTO) {
        return TimeDealRes.builder()
                .timeDealId(timeDealDTO.getTimeDealId())
                .productId(timeDealDTO.getProductId())
                .startAt(timeDealDTO.getStartAt())
                .endAt(timeDealDTO.getEndAt())
                .timeDealPrice(timeDealDTO.getTimeDealPrice())
                .discountRate(timeDealDTO.getDiscountRate())
                .timeDealStockQuantity(timeDealDTO.getQuantity())
                .status(timeDealDTO.getStatus())
                .build();
    }
}
