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
        validateTotalQuantity(totalQuantity);

        this.totalQuantity = totalQuantity;
        this.remainingQuantity = totalQuantity;
    }

    private Quantity(long totalQuantity, long remainingQuantity) {
        validateTotalQuantity(totalQuantity);
        validateRemainingQuantity(totalQuantity, remainingQuantity);

        this.totalQuantity = totalQuantity;
        this.remainingQuantity = remainingQuantity;
    }

    public static Quantity of(long totalQuantity) {
        return new Quantity(totalQuantity);
    }

    public Quantity updateTotalQuantity(long newTotalQuantity) {
        long soldQuantity = this.totalQuantity - this.remainingQuantity;

        if (newTotalQuantity < soldQuantity) {
            throw new BusinessException(TimeDealErrorCode.INVALID_TOTAL_QUANTITY_UPDATE);
        }

        long newRemaining = newTotalQuantity - soldQuantity;

        return new Quantity(newTotalQuantity, newRemaining);
    }

    public Quantity decreaseRemainingQuantity(long decreaseQuantity) {
        validateDecreaseRemainingQuantity(decreaseQuantity);

        return new Quantity(this.totalQuantity, this.remainingQuantity - decreaseQuantity);
    }

    public boolean isSoldOut() {
        return this.remainingQuantity == 0;
    }

    private void validateTotalQuantity(long totalQuantity) {
        if (totalQuantity <= 0) {
            throw new BusinessException(TimeDealErrorCode.INVALID_TOTAL_QUANTITY);
        }
    }

    private void validateRemainingQuantity(long totalQuantity, long remainingQuantity) {
        if (remainingQuantity < 0 || remainingQuantity > totalQuantity) {
            throw new BusinessException(TimeDealErrorCode.INVALID_REMAINING_QUANTITY);
        }
    }

    private void validateDecreaseRemainingQuantity(long decreaseQuantity) {
        if (decreaseQuantity <= 0) {
            throw new BusinessException(TimeDealErrorCode.TIME_DEAL_INVALID_QUANTITY);
        }

        if (this.remainingQuantity < decreaseQuantity) {
            throw new BusinessException(TimeDealErrorCode.TIME_DEAL_OUT_OF_STOCK);
        }
    }
}
