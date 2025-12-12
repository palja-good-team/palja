package com.palja.payment_service.application.validator;

import com.palja.common.exception.BusinessException;
import com.palja.common.vo.UserRole;
import com.palja.payment_service.application.command.CancelPaymentCommand;
import com.palja.payment_service.application.command.CreatePaymentCommand;
import com.palja.payment_service.application.command.FindPaymentListByConditionCommand;
import com.palja.payment_service.application.dto.external.OrderRes;
import com.palja.payment_service.application.dto.external.UserRes;
import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.vo.PaymentStatus;
import com.palja.payment_service.exception.PaymentErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentValidator {

    // ===== 결제 생성 검증 =====
    public void validateCreatePayment(CreatePaymentCommand command, OrderRes order, UserRes user) {
        validateCreatePaymentCommand(command);
        validateOrderForPayment(order, command, user);
        validateUserForPayment(user);
    }

    private void validateCreatePaymentCommand(CreatePaymentCommand command) {
        validateOrderId(command.orderId());
        validateAmount(command.amount());
        validateOrderStatus(command.orderStatus());
        log.debug("validateCreatePaymentCommand 완료: orderId={}, orderStatus={}", 
                command.orderId(), command.orderStatus());
    }

    private void validateOrderId(UUID orderId) {
        if (orderId == null) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_INFO);
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_INFO);
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_INFO);
        }
    }

    private void validateOrderStatus(String orderStatus) {
        if (orderStatus == null || orderStatus.isBlank()) {
            log.error("validateOrderStatus 실패: orderStatus가 null이거나 비어있음");
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_INFO);
        }
        if (!"CREATED".equals(orderStatus)) {
            log.error("validateOrderStatus 실패: orderStatus={}, required=CREATED", orderStatus);
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
    }

    private void validateOrderForPayment(OrderRes order, CreatePaymentCommand command, UserRes user) {
        validateOrderExists(order);
        validateOrderStatusForPayment(order, command.orderStatus());
        validateOrderAmount(order, command.amount());
        validateOrderUserId(order, user);
    }

    private void validateOrderExists(OrderRes order) {
        if (order == null) {
            throw new BusinessException(PaymentErrorCode.ORDER_NOT_FOUND);
        }
    }

    private void validateOrderStatusForPayment(OrderRes order, String requestedOrderStatus) {
        String orderStatus = order.getStatus();
        log.debug("validateOrderStatusForPayment 시작: orderId={}, 실제주문상태={}, 요청주문상태={}", 
                order.getOrderId(), orderStatus, requestedOrderStatus);

        if (orderStatus == null || orderStatus.isBlank()) {
            log.error("[에러 발생 위치 1] 주문 상태가 null이거나 비어있습니다: orderId={}", order.getOrderId());
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }

        if (!"CREATED".equalsIgnoreCase(orderStatus)) {
            log.error("[에러 발생 위치 2] 결제 가능한 주문 상태가 아닙니다: orderId={}, orderStatus={}, requiredStatus=CREATED",
                    order.getOrderId(), orderStatus);
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }

        if (requestedOrderStatus != null && !requestedOrderStatus.isBlank()) {
            if (!"CREATED".equalsIgnoreCase(requestedOrderStatus)) {
                log.error("[에러 발생 위치 3] 요청된 주문 상태가 CREATED가 아닙니다: orderId={}, requestedOrderStatus={}",
                        order.getOrderId(), requestedOrderStatus);
                throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
            }
        }
        
        log.debug("validateOrderStatusForPayment 완료: orderId={}", order.getOrderId());
    }

    private void validateOrderAmount(OrderRes order, BigDecimal paymentAmount) {
        if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("결제 금액이 null 이거나 0 이하입니다. paymentAmount={}", paymentAmount);
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_INFO);
        }

        if (order.getFinalAmount().compareTo(paymentAmount) != 0) {
            log.error("주문 금액과 결제 금액이 일치하지 않습니다. orderId={}, orderFinalAmount={}, paymentAmount={}",
                    order.getOrderId(), order.getFinalAmount(), paymentAmount);
            throw new BusinessException(PaymentErrorCode.INSUFFICIENT_FUNDS);
        }
    }


    private void validateOrderUserId(OrderRes order, UserRes user) {
        if (!order.getUserId().equals(user.getUserId())) {
            log.error("[에러 발생 위치 4] 주문의 userId와 사용자 userId가 일치하지 않음: orderId={}, orderUserId={}, userUserId={}",
                    order.getOrderId(), order.getUserId(), user.getUserId());
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
    }

    private void validateUserForPayment(UserRes user) {
        validateUserExists(user);
        validateUserStatus(user);
    }

    private void validateUserExists(UserRes user) {
        if (user == null) {
            throw new BusinessException(PaymentErrorCode.USER_NOT_FOUND);
        }
    }

    private void validateUserStatus(UserRes user) {
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
    }

    // ===== 결제 취소 검증 =====
    public void validateCancelPayment(Payment payment, CancelPaymentCommand command, UserRes user) {
        validatePaymentStatusForCancel(payment);
        validateCancelAmount(payment, command.cancelAmount());
        validateUserForCancel(user, payment);
    }

    private void validatePaymentStatusForCancel(Payment payment) {
        if (payment.getStatus() != PaymentStatus.APPROVED) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_APPROVED);
        }
    }

    private void validateCancelAmount(Payment payment, BigDecimal cancelAmount) {
        if (cancelAmount == null) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_EXCEED_AMOUNT);
        }
        if (cancelAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_EXCEED_AMOUNT);
        }
        if (cancelAmount.compareTo(payment.getAmount()) > 0) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_EXCEED_AMOUNT);
        }
        validateFullRefundOnly(payment, cancelAmount);
    }

    private void validateFullRefundOnly(Payment payment, BigDecimal cancelAmount) {
        if (cancelAmount.compareTo(payment.getAmount()) != 0) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_PARTIAL_REFUND);
        }
    }

    private void validateUserForCancel(UserRes user, Payment payment) {
        validateUserExists(user);
        validateUserOwnershipForCancel(user, payment);
    }

    private void validateUserOwnershipForCancel(UserRes user, Payment payment) {
        if (UserRole.MASTER.equals(user.getRole()) || UserRole.MANAGER.equals(user.getRole())) {
            return;
        }

        if (UserRole.CUSTOMER.equals(user.getRole())) {
            if (!payment.getUserId().equals(user.getUserId())) {
                throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
            }
            return;
        }

        throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);

    }

    // ===== 결제 삭제 검증 =====
    public void validateDeletePayment(Payment payment, UserRes user) {
        validatePaymentExists(payment);
        validatePaymentStatusForDelete(payment);
        validateUserRoleForDelete(user);
    }

    private void validatePaymentExists(Payment payment) {
        if (payment == null) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }
    }

    private void validatePaymentStatusForDelete(Payment payment) {
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_CANNOT_BE_DELETED);
        }
    }

    private void validateUserRoleForDelete(UserRes user) {
        if (user == null) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }

        if (!UserRole.MASTER.equals(user.getRole()) && !UserRole.MANAGER.equals(user.getRole())) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
    }

    // ===== 결제 조회/검색 검증 =====
    public void validateSearchPayments(FindPaymentListByConditionCommand command, UserRes user) {
        validateDateRange(command.startDate(), command.endDate());
    }

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
            }
        }
    }

    public void validateGetPayment(Payment payment, UserRes user) {
        validatePaymentExists(payment);
        validateUserAccessToPayment(user, payment);
    }

    private void validateUserAccessToPayment(UserRes user, Payment payment) {
        if (user == null) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }

        if (UserRole.MASTER.equals(user.getRole()) || UserRole.MANAGER.equals(user.getRole())) {
            return;
        }

        validateCustomerOwnership(user, payment);
    }

    private void validateCustomerOwnership(UserRes user, Payment payment) {
        if (!user.getUserId().equals(payment.getUserId())) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
    }

    // ===== PaymentLog 관련 검증=====
    public void validateSearchPaymentLogs(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
    }

    public void validateSearchPaymentLogs(LocalDateTime startDate,
                                          LocalDateTime endDate,
                                          UserRes user) {

        validateSearchPaymentLogs(startDate, endDate);

        if (user == null ||
                (!UserRole.MASTER.equals(user.getRole()) &&
                        !UserRole.MANAGER.equals(user.getRole()))) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
    }

    public void validateGetPaymentLogs(UUID paymentId, UserRes user) {
        if (paymentId == null) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }

        if (user == null ||
                (!UserRole.MASTER.equals(user.getRole()) && !UserRole.MANAGER.equals(user.getRole()))) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
    }

    public void validateDeleteOldLogs(LocalDateTime cutoffDate) {
        if (cutoffDate == null) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }

        if (cutoffDate.isAfter(LocalDateTime.now())) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
    }
}
