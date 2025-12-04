package com.palja.product_service.application.command;

public record UpdateProductCommand(
        String name,
        String description,
        Long price,
        String category
) {
}
