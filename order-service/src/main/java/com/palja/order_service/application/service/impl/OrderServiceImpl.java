package com.palja.order_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.common.vo.UserRole;
import com.palja.order_service.application.command.CreateOrderCommand;
import com.palja.order_service.application.dto.*;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.application.service.*;
import com.palja.order_service.application.service.calculator.OrderCalculator;
import com.palja.order_service.application.service.validator.OrderValidator;
import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.repository.OrderRepository;
import com.palja.order_service.domain.service.OrderDomainService;
import com.palja.order_service.domain.vo.Recipient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final TimeDealService timeDealService;
    private final CouponService couponService;
    private final UserService userService;
    private final PaymentService paymentService;
    private final OrderDomainService orderDomainService;

    private final OrderValidator orderValidator;
    private final OrderCalculator orderCalculator;

    @Transactional
    public OrderCreateRes createOrder(CreateOrderCommand command) {
        log.info("주문 생성 시작: loginId={}, productId={}", command.loginId(), command.productId());

        orderValidator.validateCreateOrderCommand(command);

        CustomerUserRes user = getUserForOrderCreation(command.loginId());
        ProductRes product = getProductForOrderCreation(command.productId(), command.quantity());
        TimeDealRes timeDeal = getValidTimeDealForOrderCreation(command);

        BigDecimal amountBeforeCoupon = calculateAmountBeforeCoupon(product, timeDeal, command.quantity());

        CouponResult couponResult = calculateValidCoupon(command.couponId(), amountBeforeCoupon);

        Recipient recipient = createRecipient(command);

        BigDecimal deliveryFee = orderDomainService.calculateDeliveryFee(amountBeforeCoupon);

        Order order = createOrder(command, user, product, timeDeal, couponResult, deliveryFee, recipient);
        orderRepository.save(order);
        log.info("주문 임시 저장 완료(결제 전): orderId={}", order.getOrderId());

        // TODO: 추후 동기 -> 비동기 고려 및 변경 예정
        deductStock(command, timeDeal);
        // TODO: 추후 동기 -> 비동기 고려 및 변경 예정
        processCouponUsage(command.couponId(), order.getOrderId());
        // TODO: 추후 동기 -> 비동기 고려 및 변경 예정
        processPayment(order, user.getUserId(), "CARD");

        orderRepository.save(order);
        log.info("주문 생성 완료: orderId={}, finalAmount={}",
                order.getOrderId(), order.getOrderAmount().getFinalAmount());

        return OrderCreateRes.from(order);
    }

    /**
     * 주문 단건 조회
     * 권한별로 접근 제어
     * - MANAGER: 모든 주문 조회 가능
     * - CUSTOMER: 본인 주문만 조회 가능
     * - COMPANY_USER: 자신이 판매한 상품의 주문만 조회 가능
     */
    public OrderDetailRes getOrder(UUID orderId, String loginId, UserRole userRole) {
        log.info("주문 조회 시작 - orderId: {}, loginId: {}, userRole: {}", orderId, loginId, userRole);

        Order order = orderRepository.findOrderByIdWithItemAndDelivery(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

        validateOrderAccess(order, loginId, userRole);

        log.info("주문 조회 성공 - orderId: {}", orderId);
        return OrderDetailRes.from(order);
    }

    // 권한별로 주문 접근 권한 검증
    private void validateOrderAccess(Order order, String loginId, UserRole userRole) {
        switch (userRole) {
            case MANAGER -> {
                // MANAGER는 모든 주문 조회 가능
                log.debug("MANAGER 권한으로 주문 조회");
            }
            case CUSTOMER -> {
                // CUSTOMER는 본인 주문만 조회 가능
                Long currentUserId = getUserIdForCustomer(loginId);
                orderValidator.validateOrderForRead(order.getUserId(), userRole, currentUserId, null, null);
            }
            case COMPANY_USER -> {
                // COMPANY_USER는 자신이 판매한 상품의 주문만 조회 가능
                UUID companyUserId = getCompanyUserIdForCompany(loginId);
                UUID productCompanyUserId = getProductCompanyUserId(order.getOrderItem().getProductId());
                orderValidator.validateOrderForRead(order.getUserId(), userRole, null, companyUserId, productCompanyUserId);
            }
            default -> throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
        }
    }
    // CUSTOMER용 userId 조회
    private Long getUserIdForCustomer(String loginId) {
        CustomerUserRes customerUser = userService.getCustomerUserByLoginId(loginId);
        return customerUser.getUserId();
    }

    // COMPANY_USER용 companyUserId 조회
    private UUID getCompanyUserIdForCompany(String loginId) {
        CompanyUserRes companyUser = userService.getCompanyUserByLoginId(loginId);
        return companyUser.getCompanyUserId();
    }

    // 상품의 판매자(companyUserId) 조회
    private UUID getProductCompanyUserId(UUID productId) {
        ProductRes product = productService.getProduct(productId);
        return product.getCompanyUserId();
    }

    // ===== 도메인 객체 생성 =====
    // 배송 정보 생성
    private Recipient createRecipient(CreateOrderCommand command) {
        return Recipient.create(
                command.delivery().recipientName(),
                command.delivery().recipientEmail(),
                command.delivery().recipientAddress(),
                command.delivery().deliveryMessage()
        );
    }

    // ====== 외부 서비스 호출 ======
    // 재고 차감
    private void deductStock(CreateOrderCommand command, TimeDealRes timeDeal) {
        // 타임딜 주문인 경우 타임딜 재고 차감
        if (command.timeDealId() != null && timeDeal != null) {
            timeDealService.deductTimeDealStock(
                    command.timeDealId(), command.quantity());

            log.info("타임딜 재고 차감 완료: timeDealId={}, quantity={}",
                    command.timeDealId(), command.quantity());
        }

        // 일반 상품 재고 차감
        productService.deductStock(command.productId(), command.quantity());

        log.info("상품 재고 차감 완료: productId={}, quantity={}",
                command.productId(), command.quantity());
    }

    // ====== private ======
    // 사용자 검증 및 정보 조회
    private CustomerUserRes getUserForOrderCreation(String loginId) {
        CustomerUserRes user = userService.getCustomerUserByLoginId(loginId);
        orderValidator.validateUserForOrderCreation(user);
        return user;
    }

    // 상품 검증 및 정보 조회
    private ProductRes getProductForOrderCreation(UUID productId, int quantity) {
        ProductRes product = productService.getProduct(productId);
        orderValidator.validateProductForOrderCreation(product, quantity);
        return product;
    }

    // 타임딜 검증 및 정보 조회 (타임딜 주문인 경우)
    private TimeDealRes getValidTimeDealForOrderCreation(CreateOrderCommand command) {
        if (command.timeDealId() == null) return null;

        TimeDealRes timeDeal = timeDealService.getTimeDeal(command.timeDealId(), command.quantity());
        orderValidator.validateCouponForOrderCreation(timeDeal, command.quantity());
        return timeDeal;
    }

    // 쿠폰 사용 전 금액 계산 (상품 총액 - 타임딜 할인)
    private BigDecimal calculateAmountBeforeCoupon(ProductRes product, TimeDealRes timeDeal, Integer quantity) {
        return orderCalculator.calculateAmountBeforeCoupon(product, timeDeal, quantity);
    }

    // 쿠폰 검증 및 할인 계산
    private CouponResult calculateValidCoupon(UUID couponId, BigDecimal amountBeforeCoupon) {
        if (couponId == null) {
            return new CouponResult(BigDecimal.ZERO, null, null);
        }

        CouponRes coupon = couponService.getCoupon(couponId);
        orderValidator.validateCouponForUsage(coupon, amountBeforeCoupon);

        BigDecimal discountAmount = orderCalculator.calculateCouponDiscount(coupon, amountBeforeCoupon);

        log.info("쿠폰 할인 적용: couponId={}, discount={}", couponId, discountAmount);

        return new CouponResult(discountAmount, couponId, coupon.getName());
    }

    // 주문 엔티티 생성 (CouponResult 사용)
    private Order createOrder(
            CreateOrderCommand command,
            CustomerUserRes user,
            ProductRes product,
            TimeDealRes timeDeal,
            CouponResult couponResult,
            BigDecimal deliveryFee,
            Recipient recipient) {

        return Order.create(
                user.getUserId(),
                command.productId(),
                product.getProductName(),
                product.getPrice(),
                command.quantity(),
                command.timeDealId(),
                timeDeal != null ? timeDeal.getTimeDealPrice() : null,
                couponResult.couponId(),
                couponResult.couponName(),
                couponResult.discountAmount(),
                deliveryFee,
                recipient
        );
    }

    // 쿠폰 사용 처리
    private void processCouponUsage(UUID couponId, UUID orderId) {
        if (couponId != null) {
            couponService.useCoupon(couponId, orderId);
        }
    }

    // 결제 처리
    private void processPayment(Order order, Long userId, String paymentMethod) {
        PaymentRes payment = paymentService.createPayment(
                order.getOrderId(),
                userId,
                order.getOrderAmount().getFinalAmount(),
                paymentMethod
        );

        order.markAsPaid(payment.getPaymentId());
        log.info("주문 결제 완료: paymentId={}, amount={}", payment.getPaymentId(), payment.getAmount());
    }

    // 쿠폰 처리 결과를 담는 내부 레코드
    private record CouponResult(
            BigDecimal discountAmount,
            UUID couponId,
            String couponName
    ) {
    }
}