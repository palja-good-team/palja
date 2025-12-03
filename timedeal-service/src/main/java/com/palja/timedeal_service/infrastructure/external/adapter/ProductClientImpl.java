package com.palja.timedeal_service.infrastructure.external.adapter;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.timedeal_service.application.dto.external.ProductInfo;
import com.palja.timedeal_service.application.port.ProductClient;
import com.palja.timedeal_service.common.TimeDealErrorCode;
import com.palja.timedeal_service.infrastructure.dto.ProductDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProductClientImpl implements ProductClient {

//    private final ProductFeignClient productFeignClient;

    @Override
    public ProductInfo getProduct(UUID productId) {
        log.info("상품 정보 요청: productId = {}", productId);

        try {
/*            // TODO. 추후 변경
            ProductDTO product = productFeignClient.getProduct(productId);*/
            ProductDTO product = mockProduct(productId);

            if (product == null) {
                log.error("[ProductClient] 상품 없음: productId={}", productId);
                throw new BusinessException(TimeDealErrorCode.PRODUCT_NOT_FOUND);
            }

            log.info("상품 정보 요청 성공: productId={}", productId);
            return product.toInfo();

        } catch (Exception e) {
            log.error("[ProductClient] 상품 조회 실패: {}", e.getMessage());
            throw new BusinessException(CommonErrorCode.FEIGN_ERROR);
        }
    }

    private ProductDTO mockProduct(UUID productId) {
        log.info("[MockProductClient] 상품 Mock 반환");

        return new ProductDTO(
                productId,
                UUID.fromString("9c2b1f77-8d4a-4c1e-b3f5-abcdef123456"),
                10000L,
                500L
        );
    }
}
