package com.palja.order_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.common.vo.UserRole;
import com.palja.order_service.application.command.CancelOrderCommand;
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

        validateOrderReadAccess(order, loginId, userRole);

        log.info("주문 조회 성공 - orderId: {}", orderId);
        return OrderDetailRes.from(order);
    }

    // 권한별로 주문 접근 권한 검증
    private void validateOrderReadAccess(Order order, String loginId, UserRole userRole) {
        switch (userRole) {
            case MANAGER -> {
                log.debug("MANAGER 권한으로 주문 조회");
            }
            case CUSTOMER -> {
                Long currentUserId = getUserIdForCustomer(loginId);
                orderValidator.validateOrderForRead(order.getUserId(), userRole, currentUserId, null, null);
            }
            case COMPANY_USER -> {
                UUID currentCompanyUserId = getCompanyUserIdForCompany(loginId);
                UUID productCompanyUserId = getProductCompanyUserId(order.getOrderItem().getProductId());
                orderValidator.validateOrderForRead(order.getUserId(), userRole, null, currentCompanyUserId, productCompanyUserId);
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
        productService.deductProductStock(command.productId(), command.quantity());

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
        orderValidator.validateTimeDealForOrderCreation(timeDeal, command.quantity());
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

    // 주문 취소
    @Transactional
    public OrderCancelRes cancelOrder(CancelOrderCommand command) {
        log.info("주문 취소 시작 - orderId: {}, loginId: {}", command.orderId(), command.CurrentUserLoginId());

        Order order = orderRepository.findOrderByIdWithItemAndDelivery(command.orderId())
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));

        validateOrderCancelAccess(order, command.CurrentUserLoginId(), command.CurrentUserRole());

        order.cancel(command.cancelReason(), command.CurrentUserLoginId());

        // 환불 처리
        processRefund(order);
        restoreStock(order);
        restoreCoupon(order);

        orderRepository.save(order);
        log.info("주문 취소 완료 - orderId: {}", command.orderId());

        return OrderCancelRes.from(order);
    }

    /**
     * 취소 권한 검증
     * - CUSTOMER: 본인 주문만 취소 가능
     * - COMPANY_USER: 자신이 판매한 상품의 주문만 취소 가능 (재고 부족, 품절 등)
     * - MANAGER: 모든 주문 취소 가능
     */
    private void validateOrderCancelAccess(Order order, String loginId, UserRole userRole) {
        switch (userRole) {
            case MANAGER -> {
                log.debug("MANAGER 권한으로 주문 취소 - orderId: {}", order.getOrderId());
            }
            case CUSTOMER -> {
                CustomerUserRes user = userService.getCustomerUserByLoginId(loginId);
                if (!user.getUserId().equals(order.getUserId())) {
                    log.warn("주문 취소 권한 없음 - 고객 불일치: userId={}, orderUserId={}",
                            user.getUserId(), order.getUserId());
                    throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
                }
                log.debug("CUSTOMER 권한으로 본인 주문 취소 - orderId: {}", order.getOrderId());
            }
            case COMPANY_USER -> {
                CompanyUserRes user = userService.getCompanyUserByLoginId(loginId);
                ProductRes product = productService.getProduct(order.getOrderItem().getProductId());

                if (!user.getCompanyUserId().equals(product.getCompanyUserId())) {
                    log.warn("주문 취소 권한 없음 - 판매자 불일치: companyUserId={}, productCompanyUserId={}",
                            user.getCompanyUserId(), product.getCompanyUserId());
                    throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
                }
                log.debug("COMPANY_USER 권한으로 판매 상품 주문 취소 - orderId: {}, companyUserId: {}",
                        order.getOrderId(), user.getCompanyUserId());
            }
            default -> {
                log.error("유효하지 않은 사용자 권한: {}", userRole);
                throw new BusinessException(OrderErrorCode.ORDER_ACCESS_DENIED);
            }
        }
    }

    /**
     * 환불 처리 (동기)
     * 결제 서비스를 직접 호출하여 환불 처리
     * 실패 시 예외 발생 → 전체 트랜잭션 롤백
     */
    private void processRefund(Order order) {
        if (order.getPaymentId() == null) {
            log.info("결제 정보가 없는 주문 - 환불 불필요: orderId={}", order.getOrderId());
            return;
        }

        try {
            log.info("환불 처리 시작 - paymentId: {}, amount: {}",
                    order.getPaymentId(), order.getOrderAmount().getFinalAmount());

            paymentService.cancelPayment(order.getOrderId(), order.getPaymentId());

            log.info("환불 처리 완료 - paymentId: {}, amount: {}",
                    order.getPaymentId(), order.getOrderAmount().getFinalAmount());
        } catch (Exception e) {
            log.error("환불 처리 실패 - orderId: {}, paymentId: {}, amount: {}",
                    order.getOrderId(), order.getPaymentId(), order.getOrderAmount().getFinalAmount(), e);
            throw new BusinessException(OrderErrorCode.REFUND_FAILED);
        }
    }

    /**
     * 재고 복구
     * - 타임딜 상품: 타임딜 서비스에 재고 복구 요청
     * - 일반 상품: 상품 서비스에 재고 복구 요청
     */
    private void restoreStock(Order order) {
        try {
            if (order.isTimeDealOrder()) {
                // 타임딜 재고 복구
                UUID timeDealId = order.getOrderItem().getTimeDealId();
                int quantity = order.getOrderItem().getQuantity();

                log.info("타임딜 재고 복구 시작 - timeDealId: {}, quantity: {}", timeDealId, quantity);
                timeDealService.restoreTimeDealStock(timeDealId, quantity);
                log.info("타임딜 재고 복구 완료 - timeDealId: {}, quantity: {}", timeDealId, quantity);
            } else {
                // 일반 상품 재고 복구
                UUID productId = order.getOrderItem().getProductId();
                int quantity = order.getOrderItem().getQuantity();

                log.info("상품 재고 복구 시작 - productId: {}, quantity: {}", productId, quantity);
                productService.restoreProductStock(productId, quantity);
                log.info("상품 재고 복구 완료 - productId: {}, quantity: {}", productId, quantity);
            }
        } catch (Exception e) {
            log.error("재고 복구 실패 - orderId: {}, timeDealOrder: {}",
                    order.getOrderId(), order.isTimeDealOrder(), e);
            // 재고 복구 실패 시에도 주문 취소는 진행
            // 별도 배치 작업으로 재고 정합성 맞추기 필요
        }
    }

    /**
     * 쿠폰 복구
     * 쿠폰을 사용했다면 쿠폰 서비스에 복구 요청
     */
    private void restoreCoupon(Order order) {
        if (order.getCouponId() == null) {
            log.debug("쿠폰 사용 없음 - 복구 불필요: orderId={}", order.getOrderId());
            return;
        }

        try {
            log.info("쿠폰 복구 시작 - couponId: {}, userId: {}",
                    order.getCouponId(), order.getUserId());

            couponService.cancelCoupon(order.getCouponId(), order.getOrderId());

            log.info("쿠폰 복구 완료 - couponId: {}, userId: {}",
                    order.getCouponId(), order.getUserId());
        } catch (Exception e) {
            log.error("쿠폰 복구 실패 - orderId: {}, couponId: {}, userId: {}",
                    order.getOrderId(), order.getCouponId(), order.getUserId(), e);
            // 쿠폰 복구 실패 시에도 주문 취소는 진행
            // 고객센터에서 수동으로 쿠폰 재발급 필요
        }
    }
}