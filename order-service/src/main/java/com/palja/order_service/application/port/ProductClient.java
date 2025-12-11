package com.palja.order_service.application.port;

import com.palja.order_service.application.dto.external.ProductRes;

import java.util.List;
import java.util.UUID;

public interface ProductClient {

    // 상품 정보 조회
    ProductRes getProduct(UUID productId);

    // 상품 재고 차감
    void deductProductStock(UUID productId, int quantity);

    // 상품 재고 복구
    void restoreProductStock(UUID productId, int quantity);

    List<UUID> getProductIdsByCompanyUserId(UUID companyUserId);
}