package com.palja.order_service.application.service.validator;

import com.palja.common.exception.BusinessException;
import com.palja.common.vo.UserRole;
import com.palja.order_service.application.command.CreateOrderCommand;
import com.palja.order_service.application.command.DeliveryCommand;
import com.palja.order_service.application.dto.CouponDiscountType;
import com.palja.order_service.application.dto.CouponUserStatus;
import com.palja.order_service.application.dto.external.CouponUserDetailRes;
import com.palja.order_service.application.dto.external.CustomerUserRes;
import com.palja.order_service.application.dto.external.ProductRes;
import com.palja.order_service.application.dto.external.TimeDealRes;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.application.port.UserClient;
import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.vo.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

// 주문 관련 검증을 담당 컴포넌트
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderValidator {

    private final UserClient userClient;

    // 검증 상수
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private static final int MAX_RECIPIENT_NAME_LENGTH = 50;
    private static final int MAX_RECIPIENT_EMAIL_LENGTH = 255;
    private static final int MAX_RECIPIENT_ADDRESS_LENGTH = 200;
    private static final int MAX_DELIVERY_MESSAGE_LENGTH = 255;
    private static final int MAX_PERCENTAGE_DISCOUNT = 100;


    // ===== Command Validation (입력 검증) =====
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

    // ===== Delivery Information Validation =====
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
        if (name.length() > MAX_RECIPIENT_NAME_LENGTH) {
            throw new BusinessException(OrderErrorCode.INVALID_RECIPIENT_INFO);
        }
    }

    private void validateRecipientEmail(String email) {
        // 이메일은 선택 사항
        if (email == null || email.isBlank()) {
            return;
        }

        if (email.length() > MAX_RECIPIENT_EMAIL_LENGTH) {
            throw new BusinessException(OrderErrorCode.INVALID_RECIPIENT_INFO);
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException(OrderErrorCode.INVALID_RECIPIENT_INFO);
        }
    }

    private void validateRecipientAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new BusinessException(OrderErrorCode.INVALID_ADDRESS);
        }
        if (address.length() > MAX_RECIPIENT_ADDRESS_LENGTH) {
            throw new BusinessException(OrderErrorCode.INVALID_ADDRESS);
        }
    }

    private void validateDeliveryMessage(String message) {
        if (message != null && message.length() > MAX_DELIVERY_MESSAGE_LENGTH) {
            throw new BusinessException(OrderErrorCode.INVALID_RECIPIENT_INFO);
        }
    }

    // ===== Customer Validation (고객 검증) =====
    // 주문 가능한 고객인지 검증
    public void validateCustomerForOrder(CustomerUserRes customer) {
        log.debug("고객 검증 시작: userId={}, status={}, role={}",
                customer.getUserId(), customer.getStatus(), customer.getRole());

        validateCustomerNotNull(customer);
        validateCustomerStatus(customer);
        validateCustomerRole(customer);

        log.debug("고객 검증 완료: userId={}", customer.getUserId());
    }
    private void validateCustomerNotNull(CustomerUserRes customer) {
        if (customer == null) {
            throw new BusinessException(OrderErrorCode.INVALID_USER_ID);
        }
    }
    private void validateCustomerStatus(CustomerUserRes customer) {
        if (!"ACTIVE".equals(customer.getStatus())) {
            throw new BusinessException(OrderErrorCode.INVALID_USER_ID);
        }
    }

    private void validateCustomerRole(CustomerUserRes customer) {
        if (UserRole.COMPANY_USER.equals(customer.getRole())) {
            throw new BusinessException(OrderErrorCode.USER_NOT_ALLOWED);
        }
    }

    // ===== Product Validation (상품 검증) =====
    // 상품 주문 가능 여부 검증
    public void validateProductForOrder(ProductRes product, int requestedQuantity) {
        log.debug("상품 검증 시작: productId={}, price={}, stock={}, requested={}",
                product.getProductId(), product.getPrice(), product.getStockQuantity(), requestedQuantity);

        validateProductNotNull(product);
        validateProductPrice(product);
        validateProductStock(product, requestedQuantity);

        log.debug("상품 검증 완료: productId={}", product.getProductId());
    }

    private void validateProductNotNull(ProductRes product) {
        if (product == null) {
            throw new BusinessException(OrderErrorCode.INVALID_PRODUCT);
        }
    }

    private void validateProductPrice(ProductRes product) {
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(OrderErrorCode.INVALID_PRODUCT_PRICE);
        }
    }

    private void validateProductStock(ProductRes product, int requestedQuantity) {
        if (product.getStockQuantity() < requestedQuantity) {
            log.warn("상품 재고 부족: productId={}, available={}, requested={}",
                    product.getProductId(), product.getStockQuantity(), requestedQuantity);
            throw new BusinessException(OrderErrorCode.INSUFFICIENT_STOCK);
        }
    }

    // ===== TimeDeal Validation (타임딜 검증) =====
    // 타임딜 주문 자격 검증
    public void validateTimeDealForOrder(TimeDealRes timeDeal, int requestedQuantity) {
        log.debug("타임딜 검증 시작: timeDealId={}, status={}, stock={}, requested={}",
                timeDeal.getTimeDealId(), timeDeal.getStatus(),
                timeDeal.getTimeDealStockQuantity(), requestedQuantity);

        validateTimeDealNotNull(timeDeal);
        validateTimeDealPeriod(timeDeal);
        validateTimeDealStatus(timeDeal);
        validateTimeDealPrice(timeDeal);
        validateTimeDealStock(timeDeal, requestedQuantity);

        log.debug("타임딜 검증 완료: timeDealId={}", timeDeal.getTimeDealId());
    }

    private void validateTimeDealNotNull(TimeDealRes timeDeal) {
        if (timeDeal == null) {
            throw new BusinessException(OrderErrorCode.INVALID_TIME_DEAL);
        }
    }

    private void validateTimeDealPeriod(TimeDealRes timeDeal) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(timeDeal.getStartAt())) {
            throw new BusinessException(OrderErrorCode.TIME_DEAL_NOT_STARTED);
        }

        if (now.isAfter(timeDeal.getEndAt())) {
            throw new BusinessException(OrderErrorCode.TIME_DEAL_EXPIRED);
        }
    }

    private void validateTimeDealStatus(TimeDealRes timeDeal) {
        if (!"OPEN".equals(timeDeal.getStatus())) {
            throw new BusinessException(OrderErrorCode.INVALID_TIME_DEAL);
        }
    }

    private void validateTimeDealPrice(TimeDealRes timeDeal) {
        if (timeDeal.getTimeDealPrice() == null
                || timeDeal.getTimeDealPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(OrderErrorCode.INVALID_TIME_DEAL);
        }
    }

    private void validateTimeDealStock(TimeDealRes timeDeal, int requestedQuantity) {
        if (timeDeal.getTimeDealStockQuantity() < requestedQuantity) {
            throw new BusinessException(OrderErrorCode.TIME_DEAL_INSUFFICIENT_STOCK);
        }
    }

    // ===== Coupon Validation (쿠폰 검증) =====
    // 쿠폰 사용 자격 검증 (상태, 타입, 기간, 최소 주문 금액)
    public void validateCouponForOrder(CouponUserDetailRes coupon) {
        log.debug("쿠폰 검증 시작: couponUserId={}, status={}, type={}, value={}",
                coupon.getCouponUserId(), coupon.getStatus(),
                coupon.getDiscountType(), coupon.getDiscountValue());

        validateCouponNotNull(coupon);
        validateCouponStatus(coupon);
        validateCouponDiscountInfo(coupon);
        validateCouponExpireAt(coupon);

        log.debug("쿠폰 검증 완료: couponUserId={}", coupon.getCouponUserId());
    }

    // 쿠폰 최소 주문 금액 검증 (금액 계산 완료 후 호출)
    public void validateCouponMinimumAmount(CouponUserDetailRes coupon, BigDecimal orderAmount) {
        if (coupon.getMinOrderAmount() == null) {
            return;
        }

        if (orderAmount == null || orderAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new BusinessException(OrderErrorCode.COUPON_MIN_AMOUNT_NOT_MET);
        }
    }

    private void validateCouponNotNull(CouponUserDetailRes coupon) {
        if (coupon == null) {
            throw new BusinessException(OrderErrorCode.COUPON_NOT_AVAILABLE);
        }
    }

    private void validateCouponStatus(CouponUserDetailRes coupon) {

        CouponUserStatus status = CouponUserStatus.valueOf(coupon.getStatus());

        switch (status) {
            case USED -> throw new BusinessException(OrderErrorCode.COUPON_ALREADY_USED);
            case EXPIRED -> throw new BusinessException(OrderErrorCode.COUPON_EXPIRED);
            case ISSUED -> {
                // 정상 사용 가능
            }
            default -> throw new BusinessException(OrderErrorCode.COUPON_NOT_AVAILABLE);
        }
    }

    private void validateCouponDiscountInfo(CouponUserDetailRes coupon) {
        if (coupon.getDiscountType() == null) {
            throw new BusinessException(OrderErrorCode.INVALID_COUPON_TYPE);
        }

        if (coupon.getDiscountValue() <= 0) {
            throw new BusinessException(OrderErrorCode.INVALID_COUPON_VALUE);
        }

        if (coupon.getDiscountType() == CouponDiscountType.PERCENTAGE
                && coupon.getDiscountValue() > MAX_PERCENTAGE_DISCOUNT) {
            throw new BusinessException(OrderErrorCode.INVALID_COUPON_VALUE);
        }
    }

    private void validateCouponExpireAt(CouponUserDetailRes coupon) {
        if (coupon.getExpireAt() != null &&
                LocalDateTime.now().isAfter(coupon.getExpireAt())) {
            throw new BusinessException(OrderErrorCode.COUPON_EXPIRED);
        }
    }

    // ===== Amount Validation (금액 검증) =====
    // 계산용 금액 유효성 검증
    public void validateAmountForCalculation(BigDecimal amount) {
        if (amount == null) {
            throw new BusinessException(OrderErrorCode.INVALID_AMOUNT);
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(OrderErrorCode.INVALID_AMOUNT);
        }
    }

    // ===== Authorization 권한 검증 =====
    // 주문 조회 권한 검증
    public void verifyOrderReadPermission(
            Order order,
            UserRole userRole,
            Long customerId,           // CUSTOMER일 때 필요
            UUID companyUserId,        // COMPANY_USER일 때 필요
            UUID sellerId              // COMPANY_USER일 때 필요
    ) {
        switch (userRole) {
            case MANAGER -> {
                log.debug("MANAGER 조회 권한 허용: orderId={}", order.getOrderId());
            }
            case CUSTOMER -> {
                verifyCustomerOwnership(order, customerId);
                log.debug("고객 조회 권한 허용: orderId={}, userId={}",
                        order.getOrderId(), customerId);
            }
            case COMPANY_USER -> {
                verifySellerOwnership(companyUserId, sellerId);
                log.debug("판매자 조회 권한 허용: orderId={}, companyUserId={}",
                        order.getOrderId(), companyUserId);
            }
            default -> {
                log.warn("잘못된 사용자 권한으로 조회 시도: role={}", userRole);
                throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
            }
        }
    }

    // 주문 취소 권한 검증
    public void verifyCancellationPermission(
            Order order,
            UserRole userRole,
            Long customerId,           // CUSTOMER일 때 필요
            UUID companyUserId,        // COMPANY_USER일 때 필요
            UUID sellerId              // COMPANY_USER일 때 필요
    ) {
        switch (userRole) {
            case MANAGER -> {
                log.debug("MANAGER 취소 권한 허용: orderId={}", order.getOrderId());
            }
            case CUSTOMER -> {
                if (!customerId.equals(order.getUserId())) {
                    log.warn("고객 취소 권한 거부: userId={}, orderUserId={}",
                            customerId, order.getUserId());
                    throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
                }
                log.debug("고객 취소 권한 허용: orderId={}, userId={}",
                        order.getOrderId(), customerId);
            }
            case COMPANY_USER -> {
                if (!companyUserId.equals(sellerId)) {
                    log.warn("판매자 취소 권한 거부: companyUserId={}, sellerId={}",
                            companyUserId, sellerId);
                    throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
                }
                log.debug("판매자 취소 권한 허용: orderId={}, companyUserId={}",
                        order.getOrderId(), companyUserId);
            }
            default -> {
                log.error("잘못된 사용자 권한으로 취소 시도: role={}", userRole);
                throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
            }
        }
    }

    // 고객 소유권 검증
    public void verifyCustomerOwnership(Order order, Long customerId) {
        if (!order.getUserId().equals(customerId)) {
            log.warn("고객 소유권 검증 실패: customerId={}, orderUserId={}",
                    customerId, order.getUserId());
            throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
        }
    }

    // 판매자 소유권 검증
    public void verifySellerOwnership(UUID companyUserId, UUID sellerId) {
        if (!companyUserId.equals(sellerId)) {
            log.warn("판매자 소유권 검증 실패: companyUserId={}, sellerId={}",
                    companyUserId, sellerId);
            throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
        }
    }

    // ===== Order Status Validation (주문 상태 검증) =====
    // 주문 상태 문자열 파싱 및 검증
    public OrderStatus validateAndParseOrderStatus(String statusStr) {
        if (statusStr == null || statusStr.isBlank()) {
            throw new BusinessException(OrderErrorCode.INVALID_ORDER_STATUS);
        }

        try {
            OrderStatus status = OrderStatus.valueOf(statusStr.toUpperCase());
            log.debug("주문 상태 파싱 완료: status={}", status);
            return status;
        } catch (IllegalArgumentException e) {
            log.error("유효하지 않은 주문 상태: statusStr={}", statusStr);
            throw new BusinessException(OrderErrorCode.INVALID_ORDER_STATUS);
        }
    }

    // CANCELED, COMPLETED로의 관리자 수동 변경 차단 (별도 API를 통해서만 처리)
    public void validateManagerTransition(OrderStatus currentStatus, OrderStatus targetStatus) {

        // 자신으로 변경 불가
        if (currentStatus == targetStatus) {
            throw new BusinessException(OrderErrorCode.SAME_STATUS_NOT_ALLOWED);
        }

        // 이미 최종 상태면 변경 불가
        if (currentStatus.isFinalState()) {
            throw new BusinessException(OrderErrorCode.FINAL_STATUS_CANNOT_CHANGE);
        }

        // 최종 상태(CANCELED, COMPLETED)로의 변경 불가
        if (targetStatus.isFinalState()) {
            throw new BusinessException(OrderErrorCode.USE_SPECIFIC_API_FOR_FINAL_STATUS);
        }
    }

    // 관리자 권한 검증 (유효한 관리자)
    public void validateManager(String loginId) {
        userClient.getMyManager(loginId);
    }
}