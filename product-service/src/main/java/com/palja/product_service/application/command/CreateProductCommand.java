package com.palja.product_service.application.command;

public record CreateProductCommand(
        String name,
        String description,
        Long price,
        Integer stock,
        String category
) {
}
