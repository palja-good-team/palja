package com.palja.product_service.application.command;

public record UpdateProductInfoCommand(
        String name,
        String description,
        Long price,
        String category
) {
}
