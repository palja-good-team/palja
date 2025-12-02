package com.palja.product_service.application.service;

import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.application.command.FindProductListByConditionCommand;
import com.palja.product_service.application.dto.CreateProductRes;
import com.palja.product_service.application.dto.ProductListByConditionRes;
import com.palja.product_service.application.dto.FindProductRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {

    CreateProductRes createProduct(CreateProductCommand createCommand);

    FindProductRes findProduct(UUID productId);

    Page<ProductListByConditionRes> findProducts(FindProductListByConditionCommand command, Pageable pageable);
}
