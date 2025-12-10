package com.palja.order_service.infrastructure.external.adapter;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.dto.response.ProductRes;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.application.service.ProductService;
import com.palja.order_service.infrastructure.external.ProductClient;
import com.palja.order_service.infrastructure.external.dto.response.ProductDTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductAdapter implements ProductService {

    private final ProductClient productClient;

    @Override
    public ProductRes getProduct(UUID productId) {
        log.debug("상품 정보 조회 요청: productId={}", productId);
        try {
            ProductDTO dto = productClient.getProduct(productId).data();
            log.info("상품 정보 조회 성공: productId={}", productId);
            return dto.toResponse();
        } catch (FeignException.NotFound e) {
            log.error("상품 정보 없음: productId={}", productId, e);
            throw new BusinessException(OrderErrorCode.PRODUCT_NOT_FOUND);
        } catch (FeignException e) {
            log.error("상품 서비스 호출 실패: productId={}, status={}, message={}",
                    productId, e.status(), e.getMessage(), e);
            throw new BusinessException(OrderErrorCode.PRODUCT_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("상품 정보 조회 중 예상치 못한 오류: productId={}, error={}",
                    productId, e.getClass().getName(), e);
            throw new BusinessException(OrderErrorCode.PRODUCT_SERVICE_UNAVAILABLE);
        }
    }

    @Override
    public void deductProductStock(UUID productId, int quantity) {
        log.info("상품 재고 차감 요청 시작: productId={}, quantity={}", productId, quantity);
        try {
            productClient.decreaseProductStock(productId, quantity);
            log.info("상품 재고 차감 성공: productId={}, quantity={}", productId, quantity);
        } catch (FeignException e) {
            log.error("상품 재고 차감 서비스 호출 실패: productId={}, quantity={}, status={}, message={}",
                    productId, quantity, e.status(), e.getMessage(), e);
            throw new BusinessException(OrderErrorCode.PRODUCT_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("상품 재고 차감 중 예상치 못한 오류: productId={}, quantity={}, error={}",
                    productId, quantity, e.getClass().getName(), e);
            throw new BusinessException(OrderErrorCode.PRODUCT_STOCK_DEDUCTION_FAILED);
        }
    }

    @Override
    public void restoreProductStock(UUID productId, int quantity) {
        log.info("상품 재고 복구 요청 시작: productId={}, quantity={}", productId, quantity);
        try {
            productClient.restoreProductStock(productId, quantity);
            log.info("상품 재고 복구 성공: productId={}, quantity={}", productId, quantity);
        } catch (FeignException e) {
            log.error("상품 재고 복구 서비스 호출 실패: productId={}, quantity={}, status={}, message={}",
                    productId, quantity, e.status(), e.getMessage(), e);
            throw new BusinessException(OrderErrorCode.PRODUCT_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("상품 재고 복구 중 예상치 못한 오류: productId={}, quantity={}, error={}",
                    productId, quantity, e.getClass().getName(), e);
            throw new BusinessException(OrderErrorCode.PRODUCT_STOCK_RESTORE_FAILED);
        }
    }

    @Override
    public List<UUID> getProductIdsByCompanyUserId(UUID companyUserId) {
        log.debug("판매자 상품 목록 조회 요청: companyUserId={}", companyUserId);
        try {
            // TODO: 판매자 id로 판매자 상품 목록 API 호출 구현
            List<UUID> productIds = productClient.getProductIdsByCompanyUserId(companyUserId);
            log.info("판매자 상품 목록 조회 성공: companyUserId={}, count={}",
                    companyUserId, productIds.size());
            return productIds;
        } catch (FeignException e) {
            log.error("상품 목록 조회 서비스 호출 실패: companyUserId={}, status={}, message={}",
                    companyUserId, e.status(), e.getMessage(), e);
            throw new BusinessException(OrderErrorCode.PRODUCT_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("판매자 상품 목록 조회 중 예상치 못한 오류: companyUserId={}, error={}",
                    companyUserId, e.getClass().getName(), e);
            throw new BusinessException(OrderErrorCode.PRODUCT_SERVICE_UNAVAILABLE);
        }
    }
}