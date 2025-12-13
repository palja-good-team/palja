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
        log.debug("validateCreatePayment 완료: orderId={}, userId={}",
                command.orderId(), user != null ? user.getUserId() : null);
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
            log.error("validateOrderId 실패: orderId가 null");
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_INFO);
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            log.error("validateAmount 실패: amount가 null");
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_INFO);
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("validateAmount 실패: amount가 0 이하 amount={}", amount);
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
        log.debug("validateOrderForPayment 완료: orderId={}, orderStatus={}, finalAmount={}",
                order.getOrderId(), order.getStatus(), order.getFinalAmount());
    }

    private void validateOrderExists(OrderRes order) {
        if (order == null) {
            log.error("validateOrderExists 실패: 주문이 존재하지 않음");
            throw new BusinessException(PaymentErrorCode.ORDER_NOT_FOUND);
        }
    }

    private void validateOrderStatusForPayment(OrderRes order, String requestedOrderStatus) {
        String orderStatus = order.getStatus();
        log.debug("validateOrderStatusForPayment 시작: orderId={}, 실제주문상태={}, 요청주문상태={}", 
                order.getOrderId(), orderStatus, requestedOrderStatus);

        if (orderStatus == null || orderStatus.isBlank()) {
            log.error("[에러 발생 위치 1] 주문 상태가 null이거나 비어있음: orderId={}", order.getOrderId());
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }

        if (!"CREATED".equalsIgnoreCase(orderStatus)) {
            log.error("[에러 발생 위치 2] 결제 가능한 주문 상태가 아님: orderId={}, orderStatus={}, requiredStatus=CREATED",
                    order.getOrderId(), orderStatus);
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }

        if (requestedOrderStatus != null && !requestedOrderStatus.isBlank()) {
            if (!"CREATED".equalsIgnoreCase(requestedOrderStatus)) {
                log.error("[에러 발생 위치 3] 요청된 주문 상태가 CREATED가 아님: orderId={}, requestedOrderStatus={}",
                        order.getOrderId(), requestedOrderStatus);
                throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
            }
        }
        
        log.debug("validateOrderStatusForPayment 완료: orderId={}", order.getOrderId());
    }

    private void validateOrderAmount(OrderRes order, BigDecimal paymentAmount) {
        if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("결제 금액이 null 이거나 0 이하: paymentAmount={}", paymentAmount);
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_INFO);
        }

        if (order.getFinalAmount().compareTo(paymentAmount) != 0) {
            log.error("주문 금액과 결제 금액이 일치하지 않음: orderId={}, orderFinalAmount={}, paymentAmount={}",
                    order.getOrderId(), order.getFinalAmount(), paymentAmount);
            throw new BusinessException(PaymentErrorCode.INSUFFICIENT_FUNDS);
        }
    }


    private void validateOrderUserId(OrderRes order, UserRes user) {
        if (user == null) {
            log.error("validateOrderUserId 실패: user가 null. orderId={}", order.getOrderId());
            throw new BusinessException(PaymentErrorCode.USER_NOT_FOUND);
        }

        if (!order.getUserId().equals(user.getUserId())) {
            log.error("[에러 발생 위치 4] 주문의 userId와 사용자 userId가 일치하지 않음: orderId={}, orderUserId={}, userUserId={}",
                    order.getOrderId(), order.getUserId(), user.getUserId());
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_INFO);
        }
    }

    private void validateUserForPayment(UserRes user) {
        validateUserExists(user);
        validateUserStatus(user);
    }

    private void validateUserExists(UserRes user) {
        if (user == null) {
            log.error("validateUserExists 실패: user가 null");
            throw new BusinessException(PaymentErrorCode.USER_NOT_FOUND);
        }
    }

    private void validateUserStatus(UserRes user) {
        if (!"ACTIVE".equals(user.getStatus())) {
            log.error("validateUserStatus 실패: userStatus={}, required=ACTIVE. userId={}, loginId={}",
                    user.getStatus(), user.getUserId(), user.getLoginId());
            throw new BusinessException(PaymentErrorCode.USER_INACTIVE);
        }
    }

    // ===== 결제 취소 검증 =====
    public void validateCancelPayment(Payment payment, CancelPaymentCommand command, UserRes user) {
        validatePaymentStatusForCancel(payment);
        validateCancelAmount(payment, command.cancelAmount());
        validateUserForCancel(user, payment);
        log.debug("validateCancelPayment 완료: paymentId={}", payment.getId());
    }

    private void validatePaymentStatusForCancel(Payment payment) {
        if (payment.getStatus() != PaymentStatus.APPROVED) {
            log.error("결제 취소 불가 상태: paymentId={}, status={}, required=APPROVED",
                    payment.getId(), payment.getStatus());
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_APPROVED);
        }
    }

    private void validateCancelAmount(Payment payment, BigDecimal cancelAmount) {
        if (cancelAmount == null) {
            log.error("취소 금액이 null: paymentId={}", payment.getId());
            throw new BusinessException(PaymentErrorCode.PAYMENT_EXCEED_AMOUNT);
        }
        if (cancelAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("취소 금액이 0 이하: paymentId={}, cancelAmount={}", payment.getId(), cancelAmount);
            throw new BusinessException(PaymentErrorCode.PAYMENT_EXCEED_AMOUNT);
        }
        if (cancelAmount.compareTo(payment.getAmount()) > 0) {
            log.error("취소 금액이 결제 금액을 초과: paymentId={}, cancelAmount={}, paymentAmount={}",
                    payment.getId(), cancelAmount, payment.getAmount());
            throw new BusinessException(PaymentErrorCode.PAYMENT_EXCEED_AMOUNT);
        }
        validateFullRefundOnly(payment, cancelAmount);
    }

    private void validateFullRefundOnly(Payment payment, BigDecimal cancelAmount) {
        if (cancelAmount.compareTo(payment.getAmount()) != 0) {
            log.error("부분 환불 불가: paymentId={}, cancelAmount={}, paymentAmount={}",
                    payment.getId(), cancelAmount, payment.getAmount());
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
                log.error("취소 권한 없음: paymentUserId={}, requestUserId={}",
                        payment.getUserId(), user.getUserId());
                throw new BusinessException(PaymentErrorCode.PAYMENT_CANCEL_FORBIDDEN);
            }
            return;
        }

        log.error("취소 권한 없는 role: role={}, userId={}", user.getRole(), user.getUserId());
        throw new BusinessException(PaymentErrorCode.PAYMENT_CANCEL_FORBIDDEN);

    }

    // ===== 결제 삭제 검증 =====
    public void validateDeletePayment(Payment payment, UserRes user) {
        validatePaymentExists(payment);
        validatePaymentStatusForDelete(payment);
        validateUserRoleForDelete(user);
        log.debug("validateDeletePayment 완료: paymentId={}", payment.getId());
    }

    private void validatePaymentExists(Payment payment) {
        if (payment == null) {
            log.error("validatePaymentExists 실패: payment가 null");
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }
    }

    private void validatePaymentStatusForDelete(Payment payment) {
        if (payment.getStatus() != PaymentStatus.PENDING) {
            log.error("삭제 불가 상태: paymentId={}, status={}, required=PENDING",
                    payment.getId(), payment.getStatus());
            throw new BusinessException(PaymentErrorCode.PAYMENT_CANNOT_BE_DELETED);
        }
    }

    private void validateUserRoleForDelete(UserRes user) {
        if (user == null) {
            log.error("validateUserRoleForDelete 실패: user가 null");
            throw new BusinessException(PaymentErrorCode.USER_NOT_FOUND);
        }

        if (!UserRole.MASTER.equals(user.getRole()) && !UserRole.MANAGER.equals(user.getRole())) {
            log.error("삭제 권한 없음: userId={}, role={}", user.getUserId(), user.getRole());
            throw new BusinessException(PaymentErrorCode.PAYMENT_DELETE_FORBIDDEN);
        }
    }

    // ===== 결제 조회/검색 검증 =====
    public void validateSearchPayments(FindPaymentListByConditionCommand command, UserRes user) {
        validateDateRange(command.startDate(), command.endDate());
        log.debug("validateSearchPayments 완료");
    }

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                log.error("기간 범위 오류: startDate={}, endDate={}", startDate, endDate);
                throw new BusinessException(PaymentErrorCode.INVALID_DATE_RANGE);
            }
        }
    }

    public void validateGetPayment(Payment payment, UserRes user) {
        validatePaymentExists(payment);
        validateUserAccessToPayment(user, payment);
        log.debug("validateGetPayment 완료: paymentId={}", payment.getId());
    }

    private void validateUserAccessToPayment(UserRes user, Payment payment) {
        if (user == null) {
            log.error("validateUserAccessToPayment 실패: user가 null. paymentId={}", payment.getId());
            throw new BusinessException(PaymentErrorCode.USER_NOT_FOUND);
        }

        if (UserRole.MASTER.equals(user.getRole()) || UserRole.MANAGER.equals(user.getRole())) {
            return;
        }

        validateCustomerOwnership(user, payment);
    }

    private void validateCustomerOwnership(UserRes user, Payment payment) {
        if (!user.getUserId().equals(payment.getUserId())) {
            log.error("조회 권한 없음: paymentId={}, paymentUserId={}, requestUserId={}",
                    payment.getId(), payment.getUserId(), user.getUserId());
            throw new BusinessException(PaymentErrorCode.PAYMENT_ACCESS_DENIED);
        }
    }

    // ===== PaymentLog 관련 검증=====
    public void validateSearchPaymentLogs(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            log.error("PaymentLog 검색 기간 오류: startDate={}, endDate={}", startDate, endDate);
            throw new BusinessException(PaymentErrorCode.INVALID_DATE_RANGE);
        }
    }

    public void validateSearchPaymentLogs(LocalDateTime startDate,
                                          LocalDateTime endDate,
                                          UserRes user) {

        validateSearchPaymentLogs(startDate, endDate);

        if (user == null ||
                (!UserRole.MASTER.equals(user.getRole()) &&
                        !UserRole.MANAGER.equals(user.getRole()))) {
            log.error("PaymentLog 검색 권한 없음: userId={}, role={}",
                    user != null ? user.getUserId() : null,
                    user != null ? user.getRole() : null);
            throw new BusinessException(PaymentErrorCode.PAYMENT_LOG_ACCESS_DENIED);
        }
        log.debug("validateSearchPaymentLogs 완료");
    }

    public void validateGetPaymentLogs(UUID paymentId, UserRes user) {
        if (paymentId == null) {
            log.error("PaymentLog 단건 조회 실패: paymentId가 null");
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
        }

        if (user == null ||
                (!UserRole.MASTER.equals(user.getRole()) && !UserRole.MANAGER.equals(user.getRole()))) {
            log.error("PaymentLog 단건 조회 권한 없음: userId={}, role={}",
                    user != null ? user.getUserId() : null,
                    user != null ? user.getRole() : null);
            throw new BusinessException(PaymentErrorCode.PAYMENT_LOG_ACCESS_DENIED);
        }
        log.debug("validateGetPaymentLogs 완료: paymentId={}", paymentId);
    }

    public void validateDeleteOldLogs(LocalDateTime cutoffDate) {
        if (cutoffDate == null) {
            log.error("cutoffDate가 null");
            throw new BusinessException(PaymentErrorCode.INVALID_CUTOFF_DATE);
        }

        if (cutoffDate.isAfter(LocalDateTime.now())) {
            log.error("cutoffDate가 미래: cutoffDate={}", cutoffDate);
            throw new BusinessException(PaymentErrorCode.INVALID_CUTOFF_DATE);
        }
        log.debug("validateDeleteOldLogs 완료: cutoffDate={}", cutoffDate);
    }
}
