package com.palja.order_service.application.service.impl;

import com.palja.order_service.application.command.CreateOrderCommand;
import com.palja.order_service.application.dto.*;
import com.palja.order_service.application.service.*;
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

    // 주문 생성
    @Transactional
    public CreateOrderRes createOrder(CreateOrderCommand command) {
        log.info("주문 생성 시작: loginId={}, productId={}",
                command.loginId(), command.productId());

        // 1. 요청 검증
        command.validate();

        // 2. 사용자 검증 및 정보 조회
        UserRes user = userService.getUserByLoginId(command.loginId());
        user.validateOrderable();

        // 3. 상품 검증 및 정보 조회
        ProductRes product = productService.getProduct(command.productId(), command.quantity());
        product.validateStock(command.quantity());

        // 4. 타임딜 검증 및 정보 조회 (타임딜 주문인 경우)
        TimeDealRes timeDeal = null;
        if (command.isTimeDealOrder()) {
            timeDeal = timeDealService.getTimeDeal(command.timeDealId(), command.quantity());
            timeDeal.validate(command.quantity());
        }

        // 5.할인 전 금액 계산 (상품 총액 - 타임딜 할인)
        BigDecimal amountBeforeCoupon = calculateAmountBeforeCoupon(
                product, timeDeal, command.quantity());

        // 6. 쿠폰 검증 및 할인 금액 계산 (쿠폰 사용 시)
        BigDecimal couponDiscountAmount = BigDecimal.ZERO;
        String couponName = null;
        if (command.hasCoupon()) {
            // 검증 및 정보 조회
            CouponRes coupon = couponService.getCoupon(
                    command.couponId());

            // 쿠폰 검증 및 할인 금액 계산
            coupon.validate(amountBeforeCoupon);
            couponDiscountAmount = coupon.calculateDiscountAmount(amountBeforeCoupon);
            couponName = coupon.getName();

            log.info("쿠폰 할인 적용: couponId={}, discount={}",
                    command.couponId(), couponDiscountAmount);
        }

        // 7. 배송 정보 생성
        Recipient recipient = createRecipient(command);
        // 8. 배송비 (쿠폰 적용가에서 판단)
        BigDecimal deliveryFee = orderDomainService.calculateDeliveryFee(amountBeforeCoupon);

        // 9. 주문 생성 (상품 및  배송 생성)
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
        if (command.hasCoupon()) {
            couponService.useCoupon(command.couponId(), order.getOrderId());
        }

        // 12. 주문 저장
        Order savedOrder = orderRepository.save(order);
        log.info("주문 생성 완료: orderId={}, finalAmount={}",
                savedOrder.getOrderId(), savedOrder.getOrderAmount().getFinalAmount());

        // TODO: 추후 동기 -> 비동기 고려 및 변경 예정
        // 12. 도메인 이벤트 발행 (결제 서비스로 전달)

        // 13. 응답 생성
        return CreateOrderRes.from(order);
    }

    /**
     * 쿠폰 할인 전 금액 계산
     */
    private BigDecimal calculateAmountBeforeCoupon(ProductRes product, TimeDealRes timeDeal, int quantity) {

        BigDecimal unitPrice = (timeDeal != null)
                ? timeDeal.getTimeDealPrice()
                : product.getPrice();

        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * 배송 정보 생성
     */
    private Recipient createRecipient(CreateOrderCommand command) {
        return Recipient.of(
                command.delivery().recipientName(),
                command.delivery().recipientEmail(),
                command.delivery().recipientAddress(),
                command.delivery().deliveryMessage()
        );
    }

    /**
     * 재고 차감
     */
    private void deductStock(CreateOrderCommand command, TimeDealRes timeDeal) {
        // 타임딜 주문인 경우 타임딜 재고 차감
        if (command.isTimeDealOrder() && timeDeal != null) {
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