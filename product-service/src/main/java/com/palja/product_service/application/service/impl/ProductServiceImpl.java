package com.palja.product_service.application.service.impl;

import com.palja.product_service.application.command.CreateProductCommand;
import com.palja.product_service.application.service.ProductService;
import com.palja.product_service.domain.repository.ProductRepository;
import com.palja.product_service.presentation.dto.res.ProductDetailRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    @Override
    public ProductDetailRes createProduct(CreateProductCommand createCommand) {
    }
}
