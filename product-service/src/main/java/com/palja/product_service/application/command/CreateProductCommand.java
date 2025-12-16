package com.palja.product_service.application.command;

import com.palja.product_service.domain.dto.req.CreateReq;

public record CreateProductCommand(
        String name,
        String description,
        Long price,
        Long stock,
        String category
) {

    public CreateReq toDomainReq() {
        return new CreateReq(name, description, price, stock, category);
    }
}
