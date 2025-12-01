package com.palja.product_service.application.service;

import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.application.dto.CreateProductRes;

public interface ProductService {

    CreateProductRes createProduct(CreateProductCommand createCommand);
}
