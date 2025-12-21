package com.palja.order_service.application.service.validator;

import com.palja.common.exception.BusinessException;
import com.palja.common.vo.UserRole;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.application.port.ProductClient;
import com.palja.order_service.application.port.UserClient;
import com.palja.order_service.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

// 주문 배송 관련 검증을 담당 컴포넌트
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDeliveryValidator {

    private final UserClient userClient;
    private final ProductClient productClient;
    private final OrderValidator orderValidator;

    /**
     * 배송 정보 등록 권한 검증
     * - MANAGER: 항상 허용
     * - COMPANY_USER: 본인이 판매한 상품의 주문일 때만 허용
     */
    public void validateDeliveryRegistrationAuthority(Order order, UserRole userRole, String loginId) {

        switch (userRole) {
            case MANAGER:
                // 매니저 허용
                return;

            case COMPANY_USER:
                // 현재 로그인한 판매자 ID
                UUID currentCompanyUserId = resolveCompanyUserId(loginId);
                // 해당 상품의 실제 판매자 ID
                UUID sellerCompanyUserId = resolveProductSellerId(order.getOrderItem().getProductId());
                // 판매자 소유권 검증
                orderValidator.verifySellerOwnership(currentCompanyUserId, sellerCompanyUserId);
                return;

            default:
                throw new BusinessException(OrderErrorCode.ORDER_MODIFICATION_DENIED);
        }
    }

    // ===== Private: Utility =====
    private UUID resolveCompanyUserId(String loginId) {
        return userClient.getMyCompanyUser(loginId).getCompanyUserId();
    }

    private UUID resolveProductSellerId(UUID productId) {
        return productClient.getProduct(productId).getCompanyUserId();
    }
}