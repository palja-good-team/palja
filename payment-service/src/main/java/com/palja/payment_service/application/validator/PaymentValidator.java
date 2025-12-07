package com.palja.payment_service.application.validator;

import com.palja.common.exception.BusinessException;
import com.palja.common.vo.UserRole;
import com.palja.payment_service.application.command.CancelPaymentCommand;
import com.palja.payment_service.application.command.CreatePaymentCommand;
import com.palja.payment_service.application.command.FindPaymentListByConditionCommand;
import com.palja.payment_service.application.dto.response.OrderRes;
import com.palja.payment_service.application.dto.response.UserRes;
import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.vo.PaymentMethod;
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
        validateOrderForPayment(order, command);
        validateUserForPayment(user, command);
    }

    private void validateCreatePaymentCommand(CreatePaymentCommand command) {
        validateOrderId(command.orderId());
        validateUserId(command.userId());
        validateAmount(command.amount());
        validateCurrency(command.currency());
        validatePaymentMethod(command.paymentMethod());
    }

    private void validateOrderId(UUID orderId) {
        if (orderId == null) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_INFO);
        }
    }

    private void validateUserId(Long userId) {
        if  (userId == null) {
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

    private void validateCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_INFO);
        }
    }

    private void validatePaymentMethod(String paymentMethod) {
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_METHOD);
        }
    }

    private void validateOrderForPayment(OrderRes order, CreatePaymentCommand command) {
        validateOrderExists(order);
        validateOrderStatusForPayment(order);
        validateOrderAmount(order, command.amount());
        validateOrderUserId(order, command.userId());
    }

    private void validateOrderExists(OrderRes order) {
        if (order == null) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }
    }

    private void validateOrderStatusForPayment(OrderRes order) {
        if (!"CREATED".equals(order.getStatus())) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
    }

    private void validateOrderAmount(OrderRes order, BigDecimal paymentAmount) {
        if (order.getFinalAmount() == null || paymentAmount == null) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }
        if (order.getFinalAmount().compareTo(paymentAmount) != 0) {
            throw new BusinessException(PaymentErrorCode.INSUFFICIENT_FUNDS);
        }
    }

    private void validateOrderUserId(OrderRes order, Long userId) {
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
    }

    private void validateUserForPayment(UserRes user, CreatePaymentCommand command) {
        validateUserExists(user);
        validateUserStatus(user);
        validateUserRoleForPayment(user);
        validateUserOwnership(user, command.userId());
    }

    private void validateUserExists(UserRes user) {
        if (user == null) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }
    }

    private void validateUserStatus(UserRes user) {
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
    }

    private void validateUserRoleForPayment(UserRes user) {
        if (UserRole.COMPANY_USER.equals(user.getRole())) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
    }

    private void validateUserOwnership(UserRes user, Long userId) {
        if (!user.getUserId().equals(userId)) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
    }

    // ===== 결제 취소 검증 =====
    public void validateCancelPayment(Payment payment, CancelPaymentCommand command, UserRes user) {
        validatePaymentStatusForCancel(payment);
        validateCancelAmount(payment, command.cancelAmount());
        validateUserForCancel(user, payment, command.userId());
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

    private void validateUserForCancel(UserRes user, Payment payment, Long userId) {
        validateUserExists(user);
        validateUserOwnershipForCancel(user, payment, userId);
    }

    private void validateUserOwnershipForCancel(UserRes user, Payment payment, Long userId) {
        if (!payment.getUserId().equals(userId)) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
        if (!user.getUserId().equals(userId)) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
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

        if (UserRole.CUSTOMER.equals(user.getRole())) {
            validateCustomerOwnership(user, payment);
            return;
        }

        if (UserRole.COMPANY_USER.equals(user.getRole())) {
            // 업체 판매자는 본인 회사 주문 결제완료만 조회 가능
            // 이 부분은 order-service에서 회사 정보를 가져와서 검증해야 함
            // 일단 기본 검증만 수행
            return;
        }

        throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
    }

    private void validateCustomerOwnership(UserRes user, Payment payment) {
        if (!user.getUserId().equals(payment.getUserId())) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
    }
}
