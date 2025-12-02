package com.palja.product_service.domain.dto.res;

import com.palja.product_service.domain.vo.Money;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class FindListByConditionRes {

    private String name;
    private Money price;
    private String category;
    private BigDecimal rating;
}
