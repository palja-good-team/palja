package com.palja.order_service.application.service;

import com.palja.order_service.application.dto.ProductRes;

import java.util.UUID;

public interface ProductService {

    // 주문 생성 시 상품 정보 조회
    ProductRes getProduct(UUID productId, int quantity);

    // 주문 확정 시 상품 재고 차감
    void deductStock(UUID productId, int quantity);

    // 주문 취소 시 상품 재고 복구
    void restoreStock(UUID productId, int quantity);
}