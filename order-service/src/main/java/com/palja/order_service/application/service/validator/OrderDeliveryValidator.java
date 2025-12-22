package com.palja.order_service.application.service.validator;

import com.palja.common.exception.BusinessException;
import com.palja.common.vo.UserRole;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// 주문 배송 관련 검증을 담당 컴포넌트
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDeliveryValidator {

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
                // 판매자 소유권 검증
                orderValidator.validateSellerOwnsProduct(loginId, order.getOrderItem().getProductId());
                return;

            default:
                throw new BusinessException(OrderErrorCode.DELIVERY_REGISTRATION_DENIED);
        }
    }
}