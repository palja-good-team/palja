package com.palja.order_service.infrastructure.external;

import com.palja.common.response.ApiResponse;
import com.palja.order_service.infrastructure.external.dto.response.ProductDTO;
import com.palja.order_service.infrastructure.external.dto.response.ProductStockDecreaseDTO;
import com.palja.order_service.infrastructure.external.dto.response.ProductStockRestoreDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "product-service", path = "/api/v1/products")
public interface ProductFeignClient {

    // 주문 서비스용 상품 정보 조회
    @GetMapping("/order/{productId}")
    ApiResponse<ProductDTO> getProduct(@PathVariable("productId") UUID productId);

    // 주문에 의한 판매 재고 차감
    @PutMapping("/order/sale/{productId}")
    ApiResponse<ProductStockDecreaseDTO> decreaseProductStock(
            @PathVariable("productId") UUID productId,
            @RequestParam("quantity") Integer quantity
    );

    // 주문 취소에 의한 재고 복구
    @PutMapping("/order/cancel/{productId}")
    ApiResponse<ProductStockRestoreDTO> restoreProductStock(
            @PathVariable("productId") UUID productId,
            @RequestParam("quantity") Integer quantity
    );

    // TODO: 판매자 id로 판매자 상품 목록 API 호출 구현
    @GetMapping("/order/seller/{companyUserId}")
    List<UUID> getProductIdsByCompanyUserId(@PathVariable("companyUserId") UUID companyUserId);
}