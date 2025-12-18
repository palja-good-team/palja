package com.palja.timedeal_service.application.port;

import com.palja.timedeal_service.application.dto.external.ProductInfo;

import java.util.UUID;

public interface ProductClient {
    ProductInfo getProduct(UUID productId);
    void decreaseStock(UUID productId, long decreaseQuantity);
    void restoreStock(UUID productId, long restoreQuantity);
}
