package com.palja.product_service.domain.repository;

public interface RedisProductRepository {

    boolean decreaseStockBySale(String key, String productId, Integer stock, Integer quantity);

    boolean adjustStock(String hashKey, String productId, Integer quantity);

    boolean deleteProductStock(String hashKey, String productId);
}
