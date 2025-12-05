package com.palja.order_service.application.service.validator;

import com.palja.common.exception.BusinessException;
import com.palja.common.vo.UserRole;
import com.palja.order_service.application.command.CreateOrderCommand;
import com.palja.order_service.application.command.DeliveryCommand;
import com.palja.order_service.application.dto.*;
import com.palja.order_service.application.exception.OrderErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 주문 관련 검증을 담당하는 컴포넌트
@Slf4j
@Component
public class OrderValidator {

    // ===== 주문 생성 검증 =====
    // 주문 생성 커맨드 검증
    public void validateCreateOrderCommand(CreateOrderCommand command) {
        validateRequiredFields(command);
        validateQuantity(command.quantity());
        validateDeliveryInfo(command.delivery());
    }

    private void validateRequiredFields(CreateOrderCommand command) {
        if (command.loginId() == null || command.loginId().isBlank()) {
            throw new BusinessException(OrderErrorCode.MISSING_REQUIRED_FIELD);
        }
        if (command.productId() == null) {
            throw new BusinessException(OrderErrorCode.MISSING_REQUIRED_FIELD);
        }
        if (command.delivery() == null) {
            throw new BusinessException(OrderErrorCode.MISSING_REQUIRED_FIELD);
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new BusinessException(OrderErrorCode.INVALID_QUANTITY);
        }
    }

    // 배송 정보 검증
    private void validateDeliveryInfo(DeliveryCommand delivery) {
        validateRecipientName(delivery.recipientName());
        validateRecipientEmail(delivery.recipientEmail());
        validateRecipientAddress(delivery.recipientAddress());
        validateDeliveryMessage(delivery.deliveryMessage());
    }

    private void validateRecipientName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(OrderErrorCode.INVALID_RECIPIENT_INFO);
        }
        if (name.length() > 50) {
            throw new BusinessException(OrderErrorCode.INVALID_RECIPIENT_INFO);
        }
    }

    private void validateRecipientEmail(String email) {
        if (email != null && email.length() > 255) {
            throw new BusinessException(OrderErrorCode.INVALID_RECIPIENT_INFO);
        }
    }

    private void validateRecipientAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new BusinessException(OrderErrorCode.INVALID_ADDRESS);
        }
        if (address.length() > 200) {
            throw new BusinessException(OrderErrorCode.INVALID_ADDRESS);
        }
    }

    private void validateDeliveryMessage(String message) {
        if (message != null && message.length() > 255) {
            throw new BusinessException(OrderErrorCode.INVALID_RECIPIENT_INFO);
        }
    }

    // ===== 사용자 검증 =====
    // 사용자 주문 가능 여부 검증
    public void validateUserOrderable(UserRes user) {
        validateUserStatus(user);
        validateUserRoleForOrder(user);
    }

    private void validateUserStatus(UserRes user) {
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException(OrderErrorCode.INVALID_USER_ID);
        }
    }

    private void validateUserRoleForOrder(UserRes user) {
        if (UserRole.COMPANY_USER.equals(user.getRole())) {
            throw new BusinessException(OrderErrorCode.USER_NOT_ALLOWED);
        }
    }

    // ===== 상품 검증 =====
    public void validateProductStock(ProductRes product, int requestedQuantity) {
        if (product.getStockQuantity() < requestedQuantity) {
            throw new BusinessException(OrderErrorCode.INSUFFICIENT_STOCK);
        }
    }

    // ===== 타임딜 검증 =====
    // 상품 재고 검증
    public void validateTimeDeal(TimeDealRes timeDeal, int requestedQuantity) {
        validateTimeDealPeriod(timeDeal);
        validateTimeDealStatus(timeDeal);
        validateTimeDealStock(timeDeal, requestedQuantity);
    }

    // 타임딜 기간 검증
    private void validateTimeDealPeriod(TimeDealRes timeDeal) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(timeDeal.getStartAt())) {
            throw new BusinessException(OrderErrorCode.TIME_DEAL_NOT_STARTED);
        }

        if (now.isAfter(timeDeal.getEndAt())) {
            throw new BusinessException(OrderErrorCode.TIME_DEAL_EXPIRED);
        }
    }

    // 타임딜 상태 검증
    private void validateTimeDealStatus(TimeDealRes timeDeal) {
        if (!"OPEN".equals(timeDeal.getStatus())) {
            throw new BusinessException(OrderErrorCode.INVALID_TIME_DEAL);
        }
    }

    // 타임딜 재고 검증
    private void validateTimeDealStock(TimeDealRes timeDeal, int requestedQuantity) {
        if (timeDeal.getTimeDealStockQuantity() < requestedQuantity) {
            throw new BusinessException(OrderErrorCode.TIME_DEAL_INSUFFICIENT_STOCK);
        }
    }

    // ===== 쿠폰 검증 =====
    public void validateCoupon(CouponRes coupon, BigDecimal orderAmount) {
        validateCouponStatus(coupon);
        validateCouponDiscountType(coupon);   // ← 추가
        validateCouponIssuePeriod(coupon);
        validateCouponMinOrderAmount(coupon, orderAmount);
    }

    // 쿠폰 상태 검증
    private void validateCouponStatus(CouponRes coupon) {
        if (!"ACTIVE".equals(coupon.getStatus())) {
            throw new BusinessException(OrderErrorCode.COUPON_NOT_AVAILABLE);
        }
    }

    // 쿠폰 할인 타입/할인값 검증
    private void validateCouponDiscountType(CouponRes coupon) {
        if (coupon.getDiscountType() == null) {
            throw new BusinessException(OrderErrorCode.INVALID_COUPON_TYPE);
        }

        if (coupon.getDiscountValue() <= 0) {
            throw new BusinessException(OrderErrorCode.INVALID_COUPON_VALUE);
        }

        if (coupon.getDiscountType() == CouponDiscountType.PERCENTAGE
                && coupon.getDiscountValue() > 100) {
            throw new BusinessException(OrderErrorCode.INVALID_COUPON_VALUE);
        }
    }

    // 쿠폰 발급 기간 검증
    private void validateCouponIssuePeriod(CouponRes coupon) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(coupon.getIssueStartAt())) {
            throw new BusinessException(OrderErrorCode.COUPON_NOT_AVAILABLE);
        }

        if (now.isAfter(coupon.getIssueEndAt())) {
            throw new BusinessException(OrderErrorCode.COUPON_EXPIRED);
        }
    }

    // 쿠폰 최소 주문 금액 검증
    private void validateCouponMinOrderAmount(CouponRes coupon, BigDecimal orderAmount) {
        if (coupon.getMinOrderAmount() == null) {
            return;
        }

        if (orderAmount == null || orderAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new BusinessException(OrderErrorCode.COUPON_MIN_AMOUNT_NOT_MET);
        }
    }
}