package com.palja.product_service.domain.repository;

import com.palja.product_service.domain.entity.Product;

import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);

    Boolean isNotUnique(Product product);

    Product getProduct(UUID productId);
}
