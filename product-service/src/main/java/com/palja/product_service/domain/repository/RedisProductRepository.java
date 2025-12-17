package com.palja.product_service.domain.repository;

import java.util.List;
import java.util.UUID;

public interface RedisProductRepository {

    boolean decreaseStockBySale(String productId, Long stock, Long quantity);

    boolean adjustStock(String productId, Long quantity);

    boolean deleteProductStock(String productId);

    boolean deleteAllStockFromRedis(List<UUID> productIds);
}
