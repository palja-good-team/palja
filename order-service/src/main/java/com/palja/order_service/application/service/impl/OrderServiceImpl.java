package com.palja.order_service.application.service.impl;

import com.palja.order_service.application.command.CreateOrderCommand;
import com.palja.order_service.application.dto.*;
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
    private final OrderDomainService orderDomainService;

    private final OrderValidator orderValidator;
    private final OrderCalculator orderCalculator;

    @Transactional
    public CreateOrderRes createOrder(CreateOrderCommand command) {
        log.info("주문 생성 시작: loginId={}, productId={}",
                command.loginId(), command.productId());

        // 1. 요청 검증
        orderValidator.validateCreateOrderCommand(command);

        // 2. 사용자 검증 및 정보 조회
        UserRes user = userService.getUserByLoginId(command.loginId());
        orderValidator.validateUserOrderable(user);

        // 3. 상품 검증 및 정보 조회
        ProductRes product = productService.getProduct(command.productId(), command.quantity());
        orderValidator.validateProductStock(product, command.quantity());

        // 4. 타임딜 검증 및 정보 조회 (타임딜 주문인 경우)
        TimeDealRes timeDeal = null;
        if (command.timeDealId() != null) {
            timeDeal = timeDealService.getTimeDeal(command.timeDealId(), command.quantity());
            orderValidator.validateTimeDeal(timeDeal, command.quantity());
        }

        // 5. 할인 전 금액 계산 (상품 총액 - 타임딜 할인)
        BigDecimal amountBeforeCoupon = orderCalculator.calculateAmountBeforeCoupon(
                product, timeDeal, command.quantity());

        // 6. 쿠폰 검증 및 할인 금액 계산 (쿠폰 사용 시)
        BigDecimal couponDiscountAmount = BigDecimal.ZERO;
        String couponName = null;
        if (command.couponId() != null) {
            CouponRes coupon = couponService.getCoupon(command.couponId());
            orderValidator.validateCoupon(coupon, amountBeforeCoupon);
            couponDiscountAmount = orderCalculator.calculateCouponDiscount(coupon, amountBeforeCoupon);
            couponName = coupon.getName();

            log.info("쿠폰 할인 적용: couponId={}, discount={}",
                    command.couponId(), couponDiscountAmount);
        }

        // 7. 배송 정보 생성
        Recipient recipient = createRecipient(command);

        // 8. 배송비 계산
        BigDecimal deliveryFee = orderDomainService.calculateDeliveryFee(amountBeforeCoupon);

        // 9. 주문 생성
        Order order = Order.create(
                user.userId(),
                command.productId(),
                product.getProductName(),
                product.getPrice(),
                command.quantity(),
                command.timeDealId(),
                timeDeal != null ? timeDeal.getTimeDealPrice() : null,
                command.couponId(),
                couponName,
                couponDiscountAmount,
                deliveryFee,
                recipient
        );

        // TODO: 추후 동기 -> 비동기 고려 및 변경 예정
        // 10. 재고 차감
        deductStock(command, timeDeal);

        // TODO: 추후 동기 -> 비동기 고려 및 변경 예정
        // 11. 쿠폰 사용 처리
        if (command.couponId() != null) {
            couponService.useCoupon(command.couponId(), order.getOrderId());
        }

        // 12. 주문 저장
        Order savedOrder = orderRepository.save(order);
        log.info("주문 생성 완료: orderId={}, finalAmount={}",
                savedOrder.getOrderId(), savedOrder.getOrderAmount().getFinalAmount());

        // TODO: 추후 동기 -> 비동기 고려 및 변경 예정
        // 13. 결제

        // 14. 응답 생성
        return CreateOrderRes.from(savedOrder);
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

    // ===== 외부 서비스 호출 =====

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
}