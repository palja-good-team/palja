package com.palja.product_service.presentation.dto.req;

import com.palja.product_service.application.command.UpdateProductInfoCommand;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateProductInfoReq {

    @Size(min = 2, max = 30)
    private String name;

    private String description;

    @Positive
    private Long price;

    @Pattern(regexp = "^\\d{9}$")
    private String category;

    public UpdateProductInfoCommand toCommand() {
        return new UpdateProductInfoCommand(name, description, price, category);
    }
}
