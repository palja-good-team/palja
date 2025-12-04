package com.palja.order_service.infrastructure.external.adapter;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.dto.ProductRes;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.application.service.ProductService;
import com.palja.order_service.infrastructure.external.dto.response.ProductDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

// 상품 서비스 클라이언트 구현
// FeignClient를 통한 외부 서비스 호출
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductAdapter implements ProductService {

    // TODO: 상품 서비스 연동 시 productClient 주입 및 구현 추가
     //private final ProductClient productClient;

    @Override
    public ProductRes getProduct(UUID productId, int quantity) {
        log.debug("상품 정보 조회: productId={}", productId);

        // TODO: 실제 상품 API 호출 (Feign)
        // ProductDTO response = productClient.getProduct(productId).data();
        // TODO: 실제 상품 서비스 연동 시 위의 코드로 교체
        // 임시 더미 데이터
        ProductDTO response = ProductDTO.dummy(productId);

        return ProductRes.from(response);
    }

    @Override
    public void deductStock(UUID productId, int quantity) {
        log.debug("재고 차감 요청: productId={}, quantity={}", productId, quantity);
        // TODO: 상품 재고 차감 API 호출 구현
        log.error("재고 차감 실패: productId={}, quantity={}", productId, quantity);
    }

    @Override
    public void restoreStock(UUID productId, int quantity) {
        log.debug("재고 복구 요청: productId={}, quantity={}", productId, quantity);
        // TODO: 상품 재고 차감 API 호출 구현
        log.error("재고 복구 실패: productId={}, quantity={}", productId, quantity);
    }
}