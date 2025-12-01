package com.palja.product_service.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Embeddable
@Getter
@EqualsAndHashCode
public class Money {

    private BigDecimal amount;

    public static Money of(Double amount) {
        if(amount == null || amount < 0)
            throw new IllegalArgumentException();

        return new Money(BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP));
    }

    protected Money() {
    }

    protected Money(BigDecimal amount) {
        this.amount = amount;
    }

    public Money plus(Money other) {
        return new Money(amount.add(other.amount));
    }

    public Money minus(Money other) {
        BigDecimal result = amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException();
        }

        return new Money(result);
    }

    public Money multiply(Money other) {
        return new Money(amount.multiply(other.amount).setScale(2, RoundingMode.HALF_UP));
    }

    public Money divide(Money other) {
        return new Money(amount.divide(other.amount,2, RoundingMode.HALF_UP));
    }

    @Override
    public String toString() {
        return amount.toPlainString();
    }
}
