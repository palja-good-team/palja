package com.palja.product_service.domain.repository;

import com.palja.product_service.domain.dto.req.FindListByConditionReq;
import com.palja.product_service.domain.entity.Product;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);

    Boolean isNotUnique(Product product);

    Product findProduct(UUID productId);

    List<Product> findProductsToCondition(FindListByConditionReq condition, Pageable pageable);
}
