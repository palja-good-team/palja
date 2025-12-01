package com.palja.timedeal_service.domain.vo;

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
    private int originalPrice;

    @Column(name = "time_deal_price", nullable = false)
    private int timeDealPrice;

    @Column(name = "discount_rate", nullable = false)
    private int discountRate;

    public Amount(int originalPrice, int timeDealPrice) {
        this.originalPrice = originalPrice;
        this.timeDealPrice = timeDealPrice;
        this.discountRate = calculateDiscount(originalPrice, timeDealPrice);
    }

    private int calculateDiscount(int originalPrice, int timeDealPrice) {
        return (int)(((double)(originalPrice - timeDealPrice) / originalPrice) * 100);
    }
}
