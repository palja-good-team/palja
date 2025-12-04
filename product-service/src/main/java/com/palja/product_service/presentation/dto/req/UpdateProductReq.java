package com.palja.product_service.presentation.dto.req;

import com.palja.product_service.application.command.UpdateProductCommand;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateProductReq {

    @Size(min = 2, max = 30)
    private String name;

    private String description;

    @Positive
    private Long price;

    private String category;

    public UpdateProductCommand toCommand() {
        return new UpdateProductCommand(name, description, price, category);
    }
}
