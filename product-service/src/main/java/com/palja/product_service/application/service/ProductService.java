package com.palja.product_service.application.service;

import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.presentation.dto.res.ProductDetailRes;

public interface ProductService {

    ProductDetailRes createProduct(CreateProductCommand createCommand);
}
