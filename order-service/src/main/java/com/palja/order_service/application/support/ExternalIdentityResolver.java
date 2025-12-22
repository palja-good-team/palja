package com.palja.order_service.application.support;

import com.palja.order_service.application.port.ProductClient;
import com.palja.order_service.application.port.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 외부 User / Product 서비스를 조회하여
 * Order 유스케이스 수행에 필요한 식별자를 해결(resolve)하는 컴포넌트
 * - 조회 전용
 * - 도메인 로직 없음
 * - 외부 의존성 캡슐화 목적
 */
@Component
@RequiredArgsConstructor
public class ExternalIdentityResolver {

    private final UserClient userClient;
    private final ProductClient productClient;

    // 로그인한 고객 userId
    public Long resolveCustomerId(String loginId) {
        return userClient.getMyCustomer(loginId).getUserId();
    }

    // 로그인한 판매자 userId
    public UUID resolveCompanyUserId(String loginId) {
        return userClient.getMyCompanyUser(loginId).getCompanyUserId();
    }

    // 상품의 판매자 userId
    public UUID resolveProductSellerId(UUID productId) {
        return productClient.getProduct(productId).getCompanyUserId();
    }
}