package com.palja.product_service.presentation.dto.req;

import com.palja.product_service.application.command.CreateProductCommand;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductReq {

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @NotNull
    @PositiveOrZero
    private Long price;

    @NotNull
    @PositiveOrZero
    private Integer stock;

    @NotEmpty
    private String category;

    @NotEmpty
    private String companyName;

    public CreateProductCommand toCommand(CreateProductReq req) {
        return new CreateProductCommand(
                req.getName(),
                req.getDescription(),
                req.getPrice(),
                req.getStock(),
                req.getCategory(),
                req.getCompanyName()
        );
    }
}
