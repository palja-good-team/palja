package com.palja.product_service.presentation.dto.req;

import com.palja.product_service.application.command.FindProductListByConditionCommand;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class FindProductListByConditionReq {

    private String name;

    @PositiveOrZero
    private Long minPrice;

    private Long maxPrice;

    private String category;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    private BigDecimal minRating;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    private BigDecimal maxRating;

    public FindProductListByConditionCommand toCommand(){
        return new FindProductListByConditionCommand(
                this.name,
                minPrice,
                maxPrice,
                this.category,
                minRating,
                maxRating
        );
    }
}
