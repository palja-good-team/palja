package com.palja.product_service.infrastructure.repository;

import com.palja.product_service.domain.dto.req.FindListByConditionReq;
import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;
    private final DslProductRepository dslProductRepository;

    @Override
    public Product save(Product product) {
        return jpaProductRepository.save(product);
    }

    @Override
    public Boolean isNotUnique(Product product) {
        return jpaProductRepository.existsByCompanyNameAndCategoryAndName(product.getCompanyName(), product.getCategory(), product.getName());
    }

    @Override
    public Product findProduct(UUID productId) {
        return jpaProductRepository.findByIdFetchStock(productId).orElseThrow();
    }

    @Override
    public List<Product> findProductsToCondition(FindListByConditionReq condition, Pageable pageable) {
        return dslProductRepository.findProductByCondition(condition, pageable);
    }
}
