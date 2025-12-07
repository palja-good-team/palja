package com.palja.product_service.domain.repository;

public interface RedisRepository {

    boolean decreaseStockBySale(String key, String productId, Integer stock, Integer quantity);

    boolean restoreStock(String hashKey, String productId, Integer quantity);
}
