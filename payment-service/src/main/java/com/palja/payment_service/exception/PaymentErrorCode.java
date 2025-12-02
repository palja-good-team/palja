package com.palja.payment_service.exception;

import com.palja.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PaymentErrorCode implements ErrorCode {

    PAYMENT_EXCEED_AMOUNT(HttpStatus.BAD_REQUEST, "취소 금액이 결제 금액을 초과할 수 없습니다."),
    PAYMENT_NOT_PARTIAL_REFUND(HttpStatus.BAD_REQUEST, "부분 환불이 불가합니다,"),
    PAYMENT_NOT_APPROVED(HttpStatus.BAD_REQUEST,"승인된 결제만 취소할 수 있습니다."),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "결제에 실패했습니다."),
    INVALID_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "유효하지 않은 결제 방법입니다."),
    INSUFFICIENT_FUNDS(HttpStatus.BAD_REQUEST, "결제에 필요한 금액이 부족합니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제를 찾을 수 없습니다."),
    PAYMENT_ALREADY_PROCESSED(HttpStatus.CONFLICT, "이미 처리된 결제입니다."),
    PAYMENT_TIMEOUT(HttpStatus.GATEWAY_TIMEOUT, "결제 처리 시간이 초과되었습니다."),
    PAYMENT_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "결제 서비스가 일시적으로 중단되었습니다."),
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
