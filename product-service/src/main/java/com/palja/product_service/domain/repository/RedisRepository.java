package com.palja.product_service.domain.repository;

public interface RedisRepository {

    boolean decreaseStockBySale(String key, String productId, Integer stock, Integer quantity);
}
