package com.palja.timedeal_service.domain.vo;

import com.palja.common.exception.BusinessException;
import com.palja.timedeal_service.common.TimeDealErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Amount {

    @Column(name = "original_price", nullable = false)
    private long originalPrice;

    @Column(name = "time_deal_price", nullable = false)
    private long timeDealPrice;

    @Column(name = "discount_rate", nullable = false)
    private int discountRate;

    private Amount(long originalPrice, long timeDealPrice) {
        validate(originalPrice, timeDealPrice);

        this.originalPrice = originalPrice;
        this.timeDealPrice = timeDealPrice;
        this.discountRate = calculateDiscount(originalPrice, timeDealPrice);
    }

    public static Amount of(long originalPrice, long timeDealPrice) {
        return new Amount(originalPrice, timeDealPrice);
    }

    private int calculateDiscount(long originalPrice, long timeDealPrice) {
        return (int)(((double)(originalPrice - timeDealPrice) / originalPrice) * 100);
    }

    private static void validate(long originalPrice, long timeDealPrice) {
        if (originalPrice <= 0) {
            throw new BusinessException(TimeDealErrorCode.INVALID_ORIGINAL_PRICE);
        }

        if (timeDealPrice <= 0) {
            throw new BusinessException(TimeDealErrorCode.INVALID_TIMEDEAL_PRICE);
        }

        if (originalPrice < timeDealPrice) {
            throw new BusinessException(TimeDealErrorCode.TIMEDEAL_PRICE_GREATER_THAN_ORIGINAL);
        }
    }
}
