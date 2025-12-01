package com.palja.product_service.infrastructure.repository;

import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;

    @Override
    public Product save(Product product) {
        return jpaProductRepository.save(product);
    }

    @Override
    public Boolean isNotUnique(Product product) {
        return jpaProductRepository.existsByCompanyNameAndCategoryAndName(product.getCompanyName(), product.getCategory(), product.getName());
    }
}
