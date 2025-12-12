package com.palja.order_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.common.response.PageResponse;
import com.palja.common.vo.UserRole;
import com.palja.order_service.application.command.CancelOrderCommand;
import com.palja.order_service.application.command.CreateOrderCommand;
import com.palja.order_service.application.dto.external.*;
import com.palja.order_service.application.dto.response.CustomerOrderSummaryRes;
import com.palja.order_service.application.dto.response.OrderCancelRes;
import com.palja.order_service.application.dto.response.OrderCreateRes;
import com.palja.order_service.application.dto.response.OrderDetailRes;
import com.palja.order_service.application.event.OrderCreatedEvent;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.application.port.*;
import com.palja.order_service.application.service.OrderService;
import com.palja.order_service.application.service.calculator.OrderPriceCalculator;
import com.palja.order_service.application.service.validator.OrderValidator;
import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.repository.OrderRepository;
import com.palja.order_service.domain.service.OrderDomainService;
import com.palja.order_service.domain.vo.OrderStatus;
import com.palja.order_service.domain.vo.Recipient;
import com.palja.order_service.presentation.dto.request.CustomerOrderSearchReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;

    private final ProductClient productClient;
    private final TimeDealClient timeDealClient;
    private final CouponClient couponClient;
    private final UserClient userClient;
    private final PaymentClient paymentClient;

    private final OrderValidator orderValidator;
    private final OrderPriceCalculator orderPriceCalculator;

    private final ApplicationEventPublisher eventPublisher;

    // ====== Order Creation Workflow ======
    /**
     * 주문 생성
     * - Command 검증
     * - 데이터 수집 및 검증
     * - 금액 계산
     * - 주문 엔티티 생성
     * - 외부 시스템 처리 (재고, 쿠폰, 결제 생성)
     * - 영속화
     */
    // TODO: Kafka Event 기반 비동기 처리 및 Saga 패턴 적용
    @Transactional
    public OrderCreateRes createOrder(CreateOrderCommand command) {
        log.info("주문 생성 시작: loginId={}, productId={}, quantity={}",
                command.loginId(), command.productId(), command.quantity());

        orderValidator.validateCreateOrderCommand(command);

        OrderCreationContext context = collectAndValidateOrderData(command);

        OrderAmountResult amountResult = calculateOrderAmounts(context);

        Order order = createOrderEntity(command, context, amountResult);
        orderRepository.save(order);
        log.info("주문 엔티티 저장 완료: orderId={}, status={}", order.getOrderId(), order.getStatus());

        // TODO: Kafka Event 발행으로 전환
        processOrderCreationExternalEvents(order, context);

        log.info("주문 생성 완료: orderId={}, status={}, finalAmount={}",
                order.getOrderId(), order.getStatus(), order.getOrderAmount().getFinalAmount());

        return OrderCreateRes.from(order);
    }

    // 주문 생성에 필요한 데이터 수집 및 검증
    private OrderCreationContext collectAndValidateOrderData(CreateOrderCommand command) {
        // 사용자 정보 조회 및 검증 (권한에 따라 다른 API 호출)
        Long userId = resolveAndValidateUserId(command.loginId(), command.userRole());
        log.debug("사용자 검증 완료: userId={}, userRole={}", userId, command.userRole());

        ProductRes product = productClient.getProduct(command.productId());
        orderValidator.validateProductForOrder(product, command.quantity());
        log.debug("상품 검증 완료: productId={}, stock={}",
                command.productId(), product.getStockQuantity());

        Optional<TimeDealRes> timeDeal = Optional.empty();
        if (command.timeDealId() != null) {
            TimeDealRes deal = timeDealClient.getTimeDeal(command.timeDealId());
            orderValidator.validateTimeDealForOrder(deal, command.quantity());
            timeDeal = Optional.of(deal);
            log.debug("타임딜 검증 완료: timeDealId={}", command.timeDealId());
        }

        // 쿠폰 조회 및 검증 (선택적)
        Optional<CouponUserRes> coupon = Optional.empty();
        if (command.couponUserId() != null) {
            CouponUserRes cou = couponClient.getCoupon(command.couponUserId());
            orderValidator.validateCouponForOrder(cou);
            coupon = Optional.of(cou);
            log.debug("쿠폰 검증 완료: couponUserId={}", command.couponUserId());
        }

        return new OrderCreationContext(
                userId,
                product,
                timeDeal,
                coupon,
                command.quantity()
        );
    }

    /**
     * 사용자 ID 조회 및 검증
     * - CUSTOMER: 고객 정보 조회 및 검증
     * - MANAGER: 매니저 정보 조회 및 검증
     * - COMPANY_USER: 주문 생성 불가
     */
    private Long resolveAndValidateUserId(String loginId, UserRole userRole) {
        switch (userRole) {
            case CUSTOMER:
                CustomerUserRes customer = userClient.getMyCustomer(loginId);
                orderValidator.validateCustomerForOrder(customer);
                return customer.getUserId();

            case MANAGER:
                ManagerUserRes manager = userClient.getMyManager(loginId);
                orderValidator.validateManagerForOrder(manager);
                return manager.getUserId();

            case COMPANY_USER:
                throw new BusinessException(
                        OrderErrorCode.ORDER_CREATION_NOT_ALLOWED_FOR_COMPANY_USER
                );

            default:
                throw new BusinessException(OrderErrorCode.INVALID_USER_ROLE);
        }
    }

    // 주문 금액 계산
    private OrderAmountResult calculateOrderAmounts(OrderCreationContext context) {
        // 상품 총액 계산 (타임딜 가격 적용)
        BigDecimal productTotal = orderPriceCalculator.calculateProductTotal(
                context.product(),
                context.timeDeal().orElse(null),
                context.quantity()
        );
        orderValidator.validateAmountForCalculation(productTotal);

        // 쿠폰 할인액 계산
        BigDecimal couponDiscount = BigDecimal.ZERO;
        if (context.coupon().isPresent()) {
            CouponUserRes coupon = context.coupon().get();

            // 쿠폰 최소 주문 금액 검증
            orderValidator.validateCouponMinimumAmount(coupon, productTotal);

            // 할인액 계산
            couponDiscount = orderPriceCalculator.calculateCouponDiscount(coupon, productTotal);
            log.debug("쿠폰 할인 계산 완료: couponUserId={}, discount={}",
                    coupon.getCouponUserId(), couponDiscount);
        }

        // 배송비 계산
        BigDecimal deliveryFee = orderDomainService.calculateDeliveryFee(productTotal);

        return new OrderAmountResult(productTotal, couponDiscount, deliveryFee);
    }

    // 주문 엔티티 생성
    private Order createOrderEntity(
            CreateOrderCommand command,
            OrderCreationContext context,
            OrderAmountResult amount) {

        Recipient recipient = createRecipient(command);

        return Order.create(
                context.userId(),
                command.productId(),
                context.product().getProductName(),
                context.product().getPrice(),
                context.quantity(),
                context.timeDeal().map(TimeDealRes::getTimeDealId).orElse(null),
                context.timeDeal().map(TimeDealRes::getTimeDealPrice).orElse(null),
                context.coupon().map(CouponUserRes::getCouponUserId).orElse(null),
                context.coupon().map(CouponUserRes::getCouponName).orElse(null),
                amount.couponDiscount(),
                amount.deliveryFee(),
                recipient
        );
    }

    // 외부 시스템 처리 (재고, 쿠폰, 결제 생성)
    private void processOrderCreationExternalEvents(Order order, OrderCreationContext context) {
        // TODO: 이벤트 발행으로 대체
        reserveInventory(order, context.timeDeal());
        applyCoupon(order.getCouponUserId(), order.getOrderId(), order.getOrderAmount().getCouponDiscountAmount());
        createPaymentAndRegister(order, context.userId());
    }

    /**
     * 재고 차감
     * - 타임딜 주문: 타임딜 재고만 차감
     * - 일반 주문: 상품 재고만 차감
     */
    // TODO: 이벤트 기반 처리
    private void reserveInventory(Order order, Optional<TimeDealRes> timeDeal) {
        UUID productId = order.getOrderItem().getProductId();
        int quantity = order.getOrderItem().getQuantity();

        try {
            if (timeDeal.isPresent()) {
                TimeDealRes deal = timeDeal.get();
                timeDealClient.deductTimeDealStock(deal.getTimeDealId(), (long) quantity);
                log.info("타임딜 재고 차감 완료: timeDealId={}, quantity={}",
                        deal.getTimeDealId(), quantity);
            } else {
                productClient.deductProductStock(productId, quantity);
                log.info("상품 재고 차감 완료: productId={}, quantity={}", productId, quantity);
            }
        } catch (Exception e) {
            log.error("재고 차감 실패: orderId={}, productId={}, isTimeDeal={}",
                    order.getOrderId(), productId, timeDeal.isPresent(), e);
            // TODO: 보상 트랜잭션 처리
            throw new BusinessException(OrderErrorCode.INVENTORY_DEDUCTION_FAILED);
        }
    }

    // 쿠폰 사용
    // TODO: 이벤트 기반 처리
    private void applyCoupon(UUID couponUserId, UUID orderId, BigDecimal couponDiscountAmount) {
        if (couponUserId == null) {
            return;
        }

        try {
            couponClient.useCoupon(couponUserId, orderId, couponDiscountAmount);
            log.info("쿠폰 사용 완료: couponUserId={}, orderId={}", couponUserId, orderId);
        } catch (Exception e) {
            log.error("쿠폰 사용 실패: orderId={}, couponUserId={}", orderId, couponUserId, e);
            // TODO: 보상 트랜잭션 처리 (재고 복구)
            throw new BusinessException(OrderErrorCode.COUPON_APPLICATION_FAILED);
        }
    }

    /**
     * 결제 생성 (결제 완료 아님)
     * - 결제 엔티티만 생성
     * - 주문 상태는 CREATED 유지
     * - 결제 Id 저장
     * - 결제 완료는 별도 API를 통해 처리
     */
    // TODO: 이벤트 기반 처리
    private void createPaymentAndRegister(Order order, Long userId) {
        try {
            PaymentCreateRes payment = paymentClient.createPayment(
                    order.getOrderId(), userId, order.getOrderAmount().getFinalAmount(), order.getStatus()
            );

            order.registerPaymentId(payment.getPaymentId());
            log.info("결제 생성 완료: orderId={}, paymentId={}, amount={}, orderStatus={}",
                    order.getOrderId(), payment.getPaymentId(), payment.getAmount(), order.getStatus());
        } catch (Exception e) {
            log.error("결제 생성 실패: orderId={}, amount={}",
                    order.getOrderId(), order.getOrderAmount().getFinalAmount(), e);
            // TODO: 보상 트랜잭션 처리 (재고 복구, 쿠폰 복구)
            throw new BusinessException(OrderErrorCode.PAYMENT_CREATION_FAILED);
        }
    }

    // 결제 실행
    // TODO: 이벤트 기반 처리
    private void executePayment(Order order, Long userId) {
        try {
            PaymentCreateRes payment = paymentClient.createPayment(
                    order.getOrderId(), userId, order.getOrderAmount().getFinalAmount(), order.getStatus()
            );

            order.markAsPaid(payment.getPaymentId());
            log.info("결제 완료: paymentId={}, amount={}",
                    payment.getPaymentId(), payment.getAmount());
        } catch (Exception e) {
            log.error("결제 실패: orderId={}, amount={}",
                    order.getOrderId(), order.getOrderAmount().getFinalAmount(), e);
            // TODO: 보상 트랜잭션 처리 (재고 복구, 쿠폰 복구)
            throw new BusinessException(OrderErrorCode.PAYMENT_FAILED);
        }
    }

    // ====== Order Cancellation Workflow ======
    /**
     * 주문 취소
     * - 주문 조회 및 권한 검증
     * - 취소 가능 상태 검증 (도메인)
     * - 주문 취소 처리 (도메인)
     * - 보상 트랜잭션 (환불, 재고, 쿠폰)
     */
    // TODO: Kafka Event 기반 비동기 처리 및 자동 보상 트랜잭션
    @Transactional
    public OrderCancelRes cancelOrder(CancelOrderCommand command) {
        log.info("주문 취소 시작: orderId={}, requestedBy={}",
                command.orderId(), command.CurrentUserLoginId());

        Order order = findOrderWithDetails(command.orderId());

        OrderAuthContext authContext = createAuthContext(order, command.CurrentUserLoginId(), command.CurrentUserRole());
        orderValidator.verifyCancellationPermission(
                order,
                command.CurrentUserRole(),
                authContext.customerId(),
                authContext.companyUserId(),
                authContext.productSellerId()
        );

        order.cancel(command.cancelReason(), command.CurrentUserLoginId());

        // TODO: Kafka Event 발행으로 전환
        processOrderCancellationExternalEvents(order, command.cancelReason());

        orderRepository.save(order);
        log.info("주문 취소 완료: orderId={}", command.orderId());

        return OrderCancelRes.from(order);
    }

    // 주문 취소
    private void processOrderCancellationExternalEvents(Order order, String cancelReason) {
        // TODO: 이벤트 발행으로 대체
        refundPayment(order, cancelReason);
        restoreInventory(order);
        restoreCoupon(order);
    }

    // 결제 환불
    // TODO: 이벤트 기반 처리
    private void refundPayment(Order order, String cancelReason) {
        if (order.getPaymentId() == null) {
            log.debug("환불할 결제 정보 없음: orderId={}", order.getOrderId());
            return;
        }

        try {
            paymentClient.cancelPayment(order.getOrderId(), order.getPaymentId(), order.getOrderAmount().getFinalAmount(), cancelReason);
            log.info("결제 환불 완료: paymentId={}, amount={}",
                    order.getPaymentId(), order.getOrderAmount().getFinalAmount());
        } catch (Exception e) {
            log.error("결제 환불 실패: orderId={}, paymentId={}",
                    order.getOrderId(), order.getPaymentId(), e);
            // TODO: 결제 환불 실패 (보상 트랜잭션 처리)
            throw new BusinessException(OrderErrorCode.REFUND_FAILED);
        }
    }

    /**
     * 재고 복구
     * - 타임딜 주문: 타임딜 재고만 복구
     * - 일반 주문: 상품 재고만 복구
     */
    // TODO: 이벤트 기반 처리
    private void restoreInventory(Order order) {
        UUID productId = order.getOrderItem().getProductId();
        int quantity = order.getOrderItem().getQuantity();
        UUID paymentId = order.getPaymentId();

        try {
            if (order.isTimeDealOrder()) {
                // 타임딜 재고만 복구
                UUID timeDealId = order.getOrderItem().getTimeDealId();
                timeDealClient.restoreTimeDealStock(timeDealId, (long) quantity);
                log.info("타임딜 재고 복구 완료: timeDealId={}, quantity={}",
                        timeDealId, quantity);
            } else {
                // 일반 상품 재고만 복구
                productClient.restoreProductStock(productId, quantity);
                log.info("상품 재고 복구 완료: productId={}, quantity={}",
                        productId, quantity);
            }
        } catch (Exception e) {
            log.error("재고 복구 실패: orderId={}, productId={}, isTimeDeal={}",
                    order.getOrderId(), productId, order.isTimeDealOrder(), e);
            // TODO: 재고 복구 실패 (보상 트랜젝션 처리)
            throw new BusinessException(OrderErrorCode.INVENTORY_RESTORE_FAILED);
        }
    }

    // 쿠폰 복구
    // TODO: 이벤트 기반 처리
    private void restoreCoupon(Order order) {
        if (order.getCouponUserId() == null) {
            return;
        }

        try {
            couponClient.cancelCoupon(order.getCouponUserId(), order.getOrderId());
            log.info("쿠폰 복구 완료: couponUserId={}, orderId={}",
                    order.getCouponUserId(), order.getOrderId());
        } catch (Exception e) {
            log.error("쿠폰 복구 실패: orderId={}, couponUserId={}",
                    order.getOrderId(), order.getCouponUserId(), e);
            // TODO: 쿠폰 복구 실패 (보상 트랜젝션 처리)
            throw new BusinessException(OrderErrorCode.COUPON_RESTORE_FAILED);
        }
    }

    // ====== Order Read Workflow ======
    /**
     * 주문 단건 조회
     * 권한별 접근 제어
     * - MANAGER: 모든 주문 조회 가능
     * - CUSTOMER: 본인 주문만 조회 가능
     * - COMPANY_USER: 자신이 판매한 상품의 주문만 조회 가능
     */
    public OrderDetailRes getOrderDetail(UUID orderId, String loginId, UserRole userRole) {
        log.info("주문 조회 시작: orderId={}, loginId={}, role={}",
                orderId, loginId, userRole);

        Order order = findOrderWithDetails(orderId);

        OrderAuthContext authContext = createAuthContext(order, loginId, userRole);
        orderValidator.verifyOrderReadPermission(
                order,
                userRole,
                authContext.customerId(),
                authContext.companyUserId(),
                authContext.productSellerId()
        );

        log.info("주문 조회 완료: orderId={}", orderId);
        return OrderDetailRes.from(order);
    }

    // ====== Customer Order List Workflow ======
    /**
     * 고객의 주문 목록 조회
     * 워크플로우:
     * 1. loginId로 userId 조회
     * 2. Repository를 통한 주문 목록 조회 (필터 적용)
     * 3. DTO 변환
     * 4. 응답 생성
     */
    @Override
    public PageResponse<CustomerOrderSummaryRes> getMyOrdersByCustomer(
            String loginId,
            CustomerOrderSearchReq request,
            Pageable pageable
    ) {
        log.info("고객 주문 목록 조회 시작: loginId={}", loginId);

        Long userId = resolveCustomerId(loginId);

        OrderStatus orderStatus = OrderStatus.from(request.getStatus());
        LocalDateTime startDateTime = toStartDateTimeOrMin(request.getStartDate());
        LocalDateTime endDateTime = toEndDateTimeOrMax(request.getEndDate());
        Page<Order> orderPage = findCustomerOrdersWithFilters(
                userId,
                orderStatus, startDateTime, endDateTime, request.getTimeDealOrder(),
                pageable
        );

        Page<CustomerOrderSummaryRes> summaryPage = orderPage.map(CustomerOrderSummaryRes::from);

        log.info("고객 주문 목록 조회 완료: userId={}, total={}, size={}",
                userId, summaryPage.getTotalElements(), summaryPage.getContent().size());

        return PageResponse.from(summaryPage);
    }

    // 필터 조건을 적용한 주문 목록 조회
    private Page<Order> findCustomerOrdersWithFilters(
            Long userId,
            OrderStatus status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Boolean timeDealOrder,
            Pageable pageable
    ) {
        return orderRepository.findCustomerOrders(
                userId,
                status,
                timeDealOrder,
                startDateTime,
                endDateTime,
                pageable
        );
    }

    // ===== Private: Authorization Context =====
    // 권한 검증을 위한 컨텍스트 구성
    private OrderAuthContext createAuthContext(Order order, String loginId, UserRole userRole) {
        Long customerId = null;
        UUID companyUserId = null;
        UUID productSellerId = null;

        switch (userRole) {
            case CUSTOMER -> {
                customerId = resolveCustomerId(loginId);
            }
            case COMPANY_USER -> {
                companyUserId = resolveCompanyUserId(loginId);
                productSellerId = resolveProductSellerId(order.getOrderItem().getProductId());
            }
        }

        return new OrderAuthContext(customerId, companyUserId, productSellerId);
    }

    // ===== Public: Utility =====
    public Order findOrderWithDetails(UUID orderId) {
        return orderRepository.findOrderByIdWithItemAndDelivery(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));
    }

    private LocalDateTime toStartDateTimeOrMin(LocalDate date) {
        if (date == null) {
            // 시스템에서 충분히 과거로 잡을 값
            return LocalDateTime.of(2025, 1, 1, 0, 0);
        }
        return date.atStartOfDay();
    }

    private LocalDateTime toEndDateTimeOrMax(LocalDate date) {
        if (date == null) {
            return LocalDate.now().atTime(23, 59, 59);
        }
        return date.atTime(23, 59, 59);
    }

    // ===== Private: Utility =====
    private Long resolveCustomerId(String loginId) {
        return userClient.getMyCustomer(loginId).getUserId();
    }

    private UUID resolveCompanyUserId(String loginId) {
        return userClient.getMyCompanyUser(loginId).getCompanyUserId();
    }

    private UUID resolveProductSellerId(UUID productId) {
        return productClient.getProduct(productId).getCompanyUserId();
    }

    // 배송 정보 생성
    private Recipient createRecipient(CreateOrderCommand command) {
        return Recipient.create(
                command.delivery().recipientName(),
                command.delivery().recipientEmail(),
                command.delivery().recipientAddress(),
                command.delivery().deliveryMessage()
        );
    }

    // ===== Internal Context Objects =====
    // 주문 생성 컨텍스트
    private record OrderCreationContext(
            Long userId,
            ProductRes product,
            Optional<TimeDealRes> timeDeal,
            Optional<CouponUserRes> coupon,
            int quantity
    ) {}

    // 권한 검증 컨텍스트
    private record OrderAuthContext(
            Long customerId,
            UUID companyUserId,
            UUID productSellerId
    ) {}

    // 주문 금액 계산 결과
    private record OrderAmountResult(
            BigDecimal productTotal,
            BigDecimal couponDiscount,
            BigDecimal deliveryFee
    ) {}
}