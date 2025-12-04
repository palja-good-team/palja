package com.palja.product_service.presentation.dto.req;

import com.palja.product_service.application.command.UpdateProductCommand;
import lombok.Getter;

@Getter
public class UpdateProductReq {

    private String name;
    private String description;
    private Long price;
    private String category;

    public UpdateProductCommand toCommand() {
        return new UpdateProductCommand(name, description, price, category);
    }
}
