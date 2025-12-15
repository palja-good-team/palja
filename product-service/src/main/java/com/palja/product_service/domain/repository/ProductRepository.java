package com.palja.product_service.domain.repository;

import com.palja.product_service.domain.dto.req.FindListByConditionReq;
import com.palja.product_service.domain.dto.req.StockScheduleDto;
import com.palja.product_service.domain.dto.res.FindProductListByConditionDto;
import com.palja.product_service.domain.dto.res.ProductInfoForOrderDto;
import com.palja.product_service.domain.dto.res.ProductInfoForTimeDealDto;
import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.entity.ProductStock;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);

    Boolean isNotUnique(String companyName, String category, String name);

    Product findProduct(UUID productId);

    ProductStock findProductStock(UUID productId);

    List<FindProductListByConditionDto> findProductsToCondition(FindListByConditionReq condition, long offset, int limit);

    Long getPageCount(FindListByConditionReq req);

    ProductInfoForTimeDealDto findProductForTimeDeal(UUID productId);

    ProductInfoForOrderDto findProductForOrder(UUID productId);

    boolean decreaseStockBySale(String productId, Integer stock, Integer quantity);

    boolean adjustStock(String productId, Integer quantity);

    void stockBulkUpdateForSchedule(Collection<StockScheduleDto> dtos);

    boolean deleteStockFromRedis(String productId);

    Product findByIdFetchStockWithLock(UUID productId, Integer quantity);

    List<UUID> findAllIdsByCompanyUserId(UUID companyUserId);

    boolean deleteAllStockFromRedis(List<UUID> productIds);

    void deleteProductForUser(UUID companyUserId);
}
