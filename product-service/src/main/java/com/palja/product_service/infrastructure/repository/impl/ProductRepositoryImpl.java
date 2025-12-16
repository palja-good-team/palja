package com.palja.product_service.infrastructure.repository.impl;

import com.palja.common.exception.BusinessException;
import com.palja.product_service.domain.dto.req.FindListByConditionReq;
import com.palja.product_service.domain.dto.req.StockScheduleDto;
import com.palja.product_service.domain.dto.res.FindProductListByConditionDto;
import com.palja.product_service.domain.dto.res.ProductInfoForOrderDto;
import com.palja.product_service.domain.dto.res.ProductInfoForTimeDealDto;
import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.entity.ProductStock;
import com.palja.product_service.domain.repository.ProductRepository;
import com.palja.product_service.domain.repository.RedisProductRepository;
import com.palja.product_service.exception.ProductErrorCode;
import com.palja.product_service.infrastructure.repository.DslProductRepository;
import com.palja.product_service.infrastructure.repository.JdbcProductRepository;
import com.palja.product_service.infrastructure.repository.JpaProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;
    private final DslProductRepository dslProductRepository;
    private final RedisProductRepository redisRepository;
    private final JdbcProductRepository jdbcProductRepository;

    @Override
    public Product save(Product product) {
        return jpaProductRepository.save(product);
    }

    @Override
    public Boolean isNotUnique(String companyName,
                               String category,
                               String name) {

        return jpaProductRepository.existsByCompanyNameAndCategory_CategoryNumberAndNameAndDeletedAtIsNull(
                companyName, category, name);
    }

    @Override
    public Product findProduct(UUID productId) {

        return jpaProductRepository
                .findByIdFetchStock(productId)
                .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    public ProductStock findProductStock(UUID productId) {
        return jpaProductRepository
                .findStockByProductId(productId)
                .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    public List<FindProductListByConditionDto> findProductsToCondition(FindListByConditionReq condition,
                                                                       long offset, int limit) {

        return dslProductRepository.findProductByCondition(condition, offset, limit);
    }

    @Override
    public Long getPageCount(FindListByConditionReq req) {

        return dslProductRepository.getPageCount(req);
    }

    @Override
    public ProductInfoForTimeDealDto findProductForTimeDeal(UUID productId) {

        return Optional.ofNullable(
                        dslProductRepository.findProductForTimeDeal(productId))
                        .orElseThrow(
                        () -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    public ProductInfoForOrderDto findProductForOrder(UUID productId) {

        return Optional.ofNullable(
                        dslProductRepository.findProductForOrder(productId))
                        .orElseThrow(
                        () -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    public boolean decreaseStockBySale(String productId, Long stock, Long quantity) {

        return redisRepository.decreaseStockBySale(productId, stock, quantity);
    }

    @Override
    public boolean adjustStock(String productId, Long quantity) {

        return redisRepository.adjustStock(productId, quantity);
    }

    @Override
    @Transactional
    public void stockBulkUpdateForSchedule(Collection<StockScheduleDto> dtos) {

        jdbcProductRepository.stockBulkUpdateForSchedule(dtos);
    }

    @Override
    public boolean deleteStockFromRedis(String productId) {

        return redisRepository.deleteProductStock(productId);
    }

    @Override
    public Product findByIdFetchStockWithLock(UUID productId,
                                              Long quantity) {

        return jpaProductRepository
                .findByIdFetchStockWithLock(productId)
                .orElseThrow(() -> new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    public List<UUID> findAllIdsByCompanyUserId(UUID companyUserId) {

        return jpaProductRepository.findAllIdsByCompanyUserId(companyUserId);

    }

    @Override
    public boolean deleteAllStockFromRedis(List<UUID> productIds) {
        return redisRepository.deleteAllStockFromRedis(productIds);
    }

    @Override
    public void deleteProductForUser(UUID companyUserId) {

        jpaProductRepository.deleteAllByCompanyUserId(companyUserId);
    }
}
