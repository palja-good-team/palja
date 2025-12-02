package com.palja.product_service.application.service;

import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.application.dto.CreateProductRes;
import com.palja.product_service.application.dto.FindProductRes;

import java.util.UUID;

public interface ProductService {

    CreateProductRes createProduct(CreateProductCommand createCommand);

    FindProductRes getProduct(UUID productId);
}
