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

    private static final long MIN_PRICE = 1L;
    private static final int PERCENT_BASE = 100;

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

    public Amount updateTimeDealPrice(long newTimeDealPrice) {
        return new Amount(this.originalPrice, newTimeDealPrice);
    }

    // ========== 계산 ==========
    private int calculateDiscount(long originalPrice, long timeDealPrice) {
        return (int) (((double) (originalPrice - timeDealPrice) / originalPrice) * 100);
    }

    // ========== 검증 ==========
    private void validate(long originalPrice, long timeDealPrice) {
        validateOriginalPrice(originalPrice);
        validateTimeDealPrice(timeDealPrice);
        validatePriceOrder(originalPrice, timeDealPrice);
    }


    private void validateOriginalPrice(long originalPrice) {
        if (isLessThanMinimumPrice(originalPrice)) {
            throw new BusinessException(TimeDealErrorCode.INVALID_ORIGINAL_PRICE);
        }
    }

    private void validateTimeDealPrice(long timeDealPrice) {
        if (isLessThanMinimumPrice(timeDealPrice)) {
            throw new BusinessException(TimeDealErrorCode.INVALID_TIMEDEAL_PRICE);
        }
    }

    private void validatePriceOrder(long originalPrice, long timeDealPrice) {
        if (!isTimeDealPriceLessThanOrEqualOriginal(originalPrice, timeDealPrice)) {
            throw new BusinessException(TimeDealErrorCode.TIMEDEAL_PRICE_GREATER_THAN_ORIGINAL);
        }
    }

    // ========== 조건식 ==========
    private boolean isLessThanMinimumPrice ( long price){
        return price < MIN_PRICE;
    }

    private boolean isTimeDealPriceLessThanOrEqualOriginal(long originalPrice, long timeDealPrice) {
        return timeDealPrice <= originalPrice;
    }
}
