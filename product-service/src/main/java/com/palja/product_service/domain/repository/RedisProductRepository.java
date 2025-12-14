package com.palja.product_service.domain.repository;

import java.util.List;
import java.util.UUID;

public interface RedisProductRepository {

    boolean decreaseStockBySale(String productId, Integer stock, Integer quantity);

    boolean adjustStock(String productId, Integer quantity);

    boolean deleteProductStock(String productId);

    boolean deleteAllStockFromRedis(List<UUID> productIds);
}
