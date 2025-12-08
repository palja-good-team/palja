package com.palja.product_service.infrastructure.repository.impl;

import com.palja.common.exception.BusinessException;
import com.palja.product_service.domain.dto.req.FindListByConditionReq;
import com.palja.product_service.domain.dto.req.StockScheduleDto;
import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.repository.ProductRepository;
import com.palja.product_service.domain.vo.Category;
import com.palja.product_service.exception.ProductErrorCode;
import com.palja.product_service.infrastructure.repository.DslProductRepository;
import com.palja.product_service.infrastructure.repository.JdbcProductRepository;
import com.palja.product_service.infrastructure.repository.JpaProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;
    private final DslProductRepository dslProductRepository;
    private final JdbcProductRepository jdbcProductRepository;

    @Override
    public Product save(Product product) {
        return jpaProductRepository.save(product);
    }

    @Override
    public Boolean isNotUnique(String companyName, Category category, String name) {
        return jpaProductRepository.existsByCompanyNameAndCategoryAndNameAndDeletedAtIsNull(
                companyName, category, name);
    }

    @Override
    public Product findProduct(UUID productId) {
        return jpaProductRepository
                .findByIdFetchStock(productId)
                .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    public List<Product> findProductsToCondition(FindListByConditionReq condition, Pageable pageable) {
        return dslProductRepository.findProductByCondition(condition, pageable);
    }

    @Override
    @Transactional
    public void stockBulkUpdateForSchedule(Collection<StockScheduleDto> dtos) {

        jdbcProductRepository.stockBulkUpdateForSchedule(dtos);
    }

    @Override
    public void restoreStock(UUID productId, Integer quantity) {
        jpaProductRepository.restoreStock(productId, quantity);
    }
}
