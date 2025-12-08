package com.palja.order_service.application.service;

import com.palja.order_service.application.dto.response.ProductRes;

import java.util.UUID;

public interface ProductService {

    // 상품 정보 조회
    ProductRes getProduct(UUID productId);

    // 상품 재고 차감
    void deductProductStock(UUID productId, int quantity);

    // 상품 재고 복구
    void restoreProductStock(UUID productId, int quantity);
}