package com.palja.product_service.application.command;

import com.palja.product_service.domain.dto.req.FindListByConditionReq;

import java.math.BigDecimal;

public record FindProductListByConditionCommand(
        String name,
        Long minPrice,
        Long maxPrice,
        String category,
        BigDecimal minRating,
        BigDecimal maxRating
) {

    public FindListByConditionReq toDomainReq() {
        return new FindListByConditionReq(
                this.name(),
                this.minPrice(),
                this.maxPrice(),
                this.category(),
                this.minRating(),
                this.maxRating()
        );
    }
}
