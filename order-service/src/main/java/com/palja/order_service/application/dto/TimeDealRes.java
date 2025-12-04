package com.palja.order_service.application.dto;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.exception.OrderErrorCode;
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
        if (timeDealDTO == null) {
            throw new BusinessException(OrderErrorCode.TIME_DEAL_NOT_FOUND);
        }

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

    // 타임딜 전체 검증
    public void validate(int requestedQuantity) {
        validatePeriod();
        validateStatus();
        validateStock(requestedQuantity);
    }

    // 타임딜 기간 내인지 확인
    public void validatePeriod() {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(startAt)) {
            throw new BusinessException(OrderErrorCode.TIME_DEAL_NOT_STARTED);
        }
        if (now.isAfter(endAt)) {
            throw new BusinessException(OrderErrorCode.TIME_DEAL_EXPIRED);
        }
    }

    // 타임딜 재고 검증
    public void validateStock(int requestedQuantity) {
        // 필요한 수량보다 적음
        if (timeDealStockQuantity < requestedQuantity) {
            throw new BusinessException(OrderErrorCode.TIME_DEAL_INSUFFICIENT_STOCK);
        }
    }

    // 타임딜 상태 검증
    public void validateStatus() {
        if (!"OPEN".equals(status)) {
            throw new BusinessException(OrderErrorCode.INVALID_TIME_DEAL);
        }
    }

    // 활성 상태 확인
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return "OPEN".equals(status)
                && now.isAfter(startAt)
                && now.isBefore(endAt)
                && timeDealStockQuantity > 0;
    }
}
