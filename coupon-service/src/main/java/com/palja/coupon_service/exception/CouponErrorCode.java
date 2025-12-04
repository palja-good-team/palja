package com.palja.coupon_service.exception;

import com.palja.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CouponErrorCode implements ErrorCode {
    // 쿠폰 생성 관련
    DUPLICATE_COUPON_NAME(HttpStatus.CONFLICT, "이미 존재하는 쿠폰명입니다."),
    INVALID_COUPON_NAME(HttpStatus.BAD_REQUEST, "쿠폰명이 유효하지 않습니다."),
    INVALID_DISCOUNT_VALUE(HttpStatus.BAD_REQUEST, "할인값이 유효하지 않습니다."),
    INVALID_DISCOUNT_TYPE(HttpStatus.BAD_REQUEST, "할인 타입이 유효하지 않습니다."),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "쿠폰 수량이 유효하지 않습니다."),
    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "발급 시작일이 종료일보다 늦을 수 없습니다."),
    INVALID_AMOUNT_RELATIONSHIP(HttpStatus.BAD_REQUEST, "최대 할인 금액이 최소 주문 금액을 초과할 수 없습니다."),
    INVALID_VALIDITY_DAYS(HttpStatus.BAD_REQUEST, "유효 기간이 유효하지 않습니다."),
    EXCEED_ACTIVE_COUPON_LIMIT(HttpStatus.BAD_REQUEST, "동시 진행 가능한 쿠폰 개수를 초과했습니다."),

    // 쿠폰 조회 관련
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠폰을 찾을 수 없습니다."),

    // 쿠폰 발급 관련
    COUPON_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "발급 가능한 쿠폰이 아닙니다."),
    COUPON_EXHAUSTED(HttpStatus.BAD_REQUEST, "쿠폰이 모두 소진되었습니다."),
    COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "쿠폰 발급 기간이 만료되었습니다."),
    COUPON_NOT_STARTED(HttpStatus.BAD_REQUEST, "쿠폰 발급 시작 전입니다."),
    DUPLICATE_COUPON_ISSUE(HttpStatus.CONFLICT, "이미 발급받은 쿠폰입니다."),

    // 쿠폰 사용 관련
    USER_COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 쿠폰을 찾을 수 없습니다."),
    COUPON_ALREADY_USED(HttpStatus.BAD_REQUEST, "이미 사용된 쿠폰입니다."),
    USER_COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "쿠폰 사용 기간이 만료되었습니다."),
    INSUFFICIENT_ORDER_AMOUNT(HttpStatus.BAD_REQUEST, "최소 주문 금액을 충족하지 못했습니다."),

    // 쿠폰 수정/삭제 관련
    CANNOT_MODIFY_DELETED_OR_EXPIRED_COUPON(HttpStatus.BAD_REQUEST, "삭제되거나 만료된 쿠폰은 수정할 수 없습니다."),
    CANNOT_MODIFY_ISSUED_COUPON(HttpStatus.BAD_REQUEST, "발급 중인 쿠폰은 수정할 수 없습니다."),
    CANNOT_DELETE_ISSUED_COUPON(HttpStatus.BAD_REQUEST, "이미 발급된 쿠폰은 삭제할 수 없습니다."),

    // 권한 관련
    INSUFFICIENT_PERMISSION(HttpStatus.FORBIDDEN, "쿠폰 관리 권한이 없습니다.");;

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
