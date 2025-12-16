package com.palja.payment_service.exception;

import com.palja.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PaymentErrorCode implements ErrorCode {

    // 외부 서비스 & 연동 오류
    ORDER_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "주문 서비스가 일시적으로 중단되었습니다."),
    USER_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "사용자 서비스가 일시적으로 중단되었습니다."),
    PAYMENT_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "결제 서비스가 일시적으로 중단되었습니다."),
    PAYMENT_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "결제 처리 시간이 초과되었습니다."),

    // 리소스 조회 실패
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제를 찾을 수 없습니다."),
    PAYMENT_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 로그를 찾을 수 없습니다."),

    // 권한
    PAYMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 결제에 접근할 권한이 없습니다."),
    PAYMENT_CANCEL_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 결제를 취소할 권한이 없습니다."),
    PAYMENT_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "결제를 삭제할 권한이 없습니다."),
    PAYMENT_LOG_ACCESS_DENIED(HttpStatus.FORBIDDEN, "결제 로그에 접근할 권한이 없습니다."),
    USER_INACTIVE(HttpStatus.FORBIDDEN, "비활성화된 사용자입니다."),

    // 검증
    INVALID_PAYMENT_INFO(HttpStatus.BAD_REQUEST, "결제 정보가 일치하지 않습니다."),
    INVALID_PAYMENT_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 결제 상태입니다."),
    INVALID_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "유효하지 않은 결제 방법입니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "조회 기간이 올바르지 않습니다."),
    INVALID_CUTOFF_DATE(HttpStatus.BAD_REQUEST, "삭제 기준일이 올바르지 않습니다."),

    INSUFFICIENT_FUNDS(HttpStatus.BAD_REQUEST, "결제에 필요한 금액이 부족합니다."),
    PAYMENT_EXCEED_AMOUNT(HttpStatus.BAD_REQUEST, "취소 금액이 결제 금액을 초과할 수 없습니다."),
    PAYMENT_NOT_PARTIAL_REFUND(HttpStatus.BAD_REQUEST, "부분 환불이 불가합니다."),
    PAYMENT_NOT_APPROVED(HttpStatus.BAD_REQUEST, "승인된 결제만 취소할 수 있습니다."),
    PAYMENT_ALREADY_PROCESSED(HttpStatus.CONFLICT, "이미 처리된 결제입니다."),
    PAYMENT_CANNOT_BE_DELETED(HttpStatus.BAD_REQUEST, "PENDING 상태인 결제만 삭제할 수 있습니다."),

    // 결제 처리 실패 & 시스템 오류
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "결제에 실패했습니다."),
    PAYMENT_CANCEL_FAILED(HttpStatus.BAD_REQUEST, "결제 취소에 실패했습니다."),
    PAYMENT_SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "결제 시스템 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
