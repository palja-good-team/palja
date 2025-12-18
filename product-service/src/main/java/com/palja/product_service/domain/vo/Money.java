package com.palja.product_service.domain.vo;

import com.palja.common.exception.BusinessException;
import com.palja.product_service.exception.ProductErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
@Getter
@EqualsAndHashCode
public class Money {

    @Column(name = "price")
    private Long amount;

    public static Money of(Long amount) {
        if(amount == null || amount < 0)
            throw new BusinessException(ProductErrorCode.INVALID_PRICE);

        return new Money(amount);
    }

    protected Money() {
    }

    protected Money(Long amount) {
        this.amount = amount;
    }

    public Money plus(Long other) {
        return new Money(amount + other);
    }

    public Money minus(Long other) {

        long result = amount - other;
        if (result < 0) {
            throw new BusinessException(ProductErrorCode.INVALID_PRICE);
        }

        return new Money(result);
    }

    public Money multiply(Double other) {
        return new Money( (long)(amount * other) );
    }

    public Money divide(Double other) {
        return new Money((long)(amount/other));
    }

    @Override
    public String toString() {
        return amount.toString();
    }
}
