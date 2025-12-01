package com.palja.product_service.domain.repository;

import com.palja.product_service.domain.entity.Product;

public interface ProductRepository {

    Product save(Product product);

    Boolean isNotUnique(Product product);
}
