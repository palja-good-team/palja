package com.palja.product_service.presentation.dto.req;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class RatingProductReq {

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    private BigDecimal rate;
}
