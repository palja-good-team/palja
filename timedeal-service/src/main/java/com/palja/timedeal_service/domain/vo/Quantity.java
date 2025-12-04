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
public class Quantity {

    @Column(name = "total_quantity", nullable = false)
    private long totalQuantity;

    @Column(name = "remaining_quantity", nullable = false)
    private long remainingQuantity;


    private Quantity(long totalQuantity) {
        validate(totalQuantity);

        this.totalQuantity = totalQuantity;
        this.remainingQuantity = totalQuantity;
    }

    public static Quantity of(long totalQuantity) {
        return new Quantity(totalQuantity);
    }

    private static void validate(long totalQuantity) {
        if (totalQuantity <= 0) {
            throw new BusinessException(TimeDealErrorCode.INVALID_TOTAL_QUANTITY);
        }
    }
}
