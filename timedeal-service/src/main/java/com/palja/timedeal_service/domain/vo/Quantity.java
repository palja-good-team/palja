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

    private static final long MIN_TOTAL_QUANTITY = 1L;
    private static final long MIN_DELTA_QUANTITY = 1L;
    private static final long SOLD_OUT_REMAINING = 0L;

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
        validateRemainingInRange(totalQuantity, remainingQuantity);

        this.totalQuantity = totalQuantity;
        this.remainingQuantity = remainingQuantity;
    }

    public static Quantity of(long totalQuantity) {
        return new Quantity(totalQuantity);
    }

    public Quantity updateTotalQuantity(long newTotalQuantity) {
        long soldQuantity = calculateSoldQuantity();

        validateTotalQuantityUpdate(newTotalQuantity, soldQuantity);

        long newRemaining = calculateNewRemaining(newTotalQuantity, soldQuantity);

        return new Quantity(newTotalQuantity, newRemaining);
    }

    public Quantity decreaseRemainingQuantity(long decreaseQuantity) {
        validateDecreaseRemainingQuantity(decreaseQuantity);

        long decreasedRemaining = minusRemaining(decreaseQuantity);

        return new Quantity(this.totalQuantity, decreasedRemaining);
    }

    public Quantity restoreRemainingQuantity(long restoreQuantity) {
        validateRestoreRemainingQuantity(restoreQuantity);

        long restoredRemaining = plusRemaining(restoreQuantity);

        return new Quantity(this.totalQuantity, restoredRemaining);
    }

    public boolean isSoldOut() {
        return this.remainingQuantity == SOLD_OUT_REMAINING;
    }

    // ========== 계산 ==========
    private long calculateSoldQuantity() {
        return this.totalQuantity - this.remainingQuantity;
    }

    private long calculateNewRemaining(long newTotalQuantity, long soldQuantity) {
        return newTotalQuantity - soldQuantity;
    }

    private long minusRemaining(long decreaseQuantity) {
        return this.remainingQuantity - decreaseQuantity;
    }

    private long plusRemaining(long restoreQuantity) {
        return this.remainingQuantity + restoreQuantity;
    }

    // ========== 검증 ==========
    private void validateTotalQuantity(long totalQuantity) {
        if (!isValidTotalQuantity(totalQuantity)) {
            throw new BusinessException(TimeDealErrorCode.INVALID_TOTAL_QUANTITY);
        }
    }

    private void validateRemainingInRange(long totalQuantity, long remainingQuantity) {
        if (!isRemainingWithinTotal(totalQuantity, remainingQuantity)) {
            throw new BusinessException(TimeDealErrorCode.INVALID_REMAINING_QUANTITY);
        }
    }

    private void validateTotalQuantityUpdate(long newTotalQuantity, long soldQuantity) {
        if (!canUpdateTotalQuantity(newTotalQuantity, soldQuantity)) {
            throw new BusinessException(TimeDealErrorCode.INVALID_TOTAL_QUANTITY_UPDATE);
        }
    }
    private void validateDecreaseRemainingQuantity(long decreaseQuantity) {
        validateDeltaQuantity(decreaseQuantity);

        if (this.remainingQuantity < decreaseQuantity) {
            throw new BusinessException(TimeDealErrorCode.TIME_DEAL_OUT_OF_STOCK);
        }
    }

    private void validateRestoreRemainingQuantity(long restoreQuantity) {
        validateDeltaQuantity(restoreQuantity);

        if (this.remainingQuantity + restoreQuantity > this.totalQuantity) {
            throw new BusinessException(TimeDealErrorCode.TIME_DEAL_STOCK_OVERFLOW);
        }
    }

    private void validateDeltaQuantity(long deltaQuantity) {
        if (isLessThanMinimumDelta(deltaQuantity)) {
            throw new BusinessException(TimeDealErrorCode.TIME_DEAL_INVALID_QUANTITY);
        }
    }

    // ========== 조건식 ==========
    private boolean isValidTotalQuantity(long totalQuantity) {
        return totalQuantity >= MIN_TOTAL_QUANTITY;
    }

    private boolean isRemainingWithinTotal(long totalQuantity, long remainingQuantity) {
        return remainingQuantity >= 0 && remainingQuantity <= totalQuantity;
    }

    private boolean canUpdateTotalQuantity(long newTotalQuantity, long soldQuantity) {
        return newTotalQuantity >= soldQuantity;
    }

    private boolean isLessThanMinimumDelta(long deltaQuantity) {
        return deltaQuantity < MIN_DELTA_QUANTITY;
    }
}
