package com.palja.product_service.infrastructure.repository.impl;

import com.palja.common.exception.BusinessException;
import com.palja.product_service.domain.dto.req.StockScheduleDto;
import com.palja.product_service.domain.dto.req.FindListByConditionReq;
import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.repository.ProductRepository;
import com.palja.product_service.domain.vo.Category;
import com.palja.product_service.exception.ProductErrorCode;
import com.palja.product_service.infrastructure.repository.DslProductRepository;
import com.palja.product_service.infrastructure.repository.JpaProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;
    private final DslProductRepository dslProductRepository;
    private final JdbcTemplate jdbcTemplate;

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
    public void stockBulkUpdateForSchedule(Collection<StockScheduleDto> dtos) {

        String sql = """
                UPDATE palja_product.p_product_stock ps 
                SET quantity = ? 
                FROM palja_product.p_product p 
                WHERE ps.product_id = p.product_id AND ps.product_id = ?
                """;
        jdbcTemplate.batchUpdate(
                sql, dtos, dtos.size(),
                (ps, dto) -> {
                    ps.setInt(1, dto.getQuantity());
                    ps.setObject(2, dto.getProductId());
                }
        );
    }

    @Override
    public void restoreStock(UUID productId, Integer quantity) {
        jpaProductRepository.restoreStock(productId, quantity);
    }
}
