package com.palja.product_service.presentation.dto.req;

import com.palja.product_service.application.command.CreateProductCommand;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateProductReq {

    @NotEmpty(message = "상품의 이름은 필수항목입니다")
    private String name;

    @NotEmpty(message = "상품의 설명은 필수항목입니다")
    private String description;

    @NotNull(message = "상품의 가격은 필수항목입니다")
    @PositiveOrZero(message = "가격은 양수여야 합니다")
    private Long price;

    @NotNull(message = "상품의 재고는 필수항목입니다")
    @PositiveOrZero(message = "재고는 양수여야 합니다")
    private Integer stock;

    @NotEmpty(message = "상품의 카테고리는 필수항목이며, FOOD, TOOL, CLOTHING 를 지원합니다")
    private String category;

    public CreateProductCommand toCommand(CreateProductReq req) {
        return new CreateProductCommand(
                req.getName(),
                req.getDescription(),
                req.getPrice(),
                req.getStock(),
                req.getCategory()
        );
    }
}
