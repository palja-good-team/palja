package com.palja.order_service.application.exception;

import com.palja.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    // 사용자
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    // ===== 주문 관련 =====
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 상품 정보를 찾을 수 없습니다."),
    ORDER_DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "배송 정보를 찾을 수 없습니다."),
    INVALID_ORDER_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "주문 상태를 변경할 수 없습니다."),
    ORDER_ALREADY_PAID(HttpStatus.CONFLICT, "이미 결제 완료된 주문입니다."),
    ORDER_ALREADY_CANCELED(HttpStatus.CONFLICT, "이미 취소된 주문입니다."),
    ORDER_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "취소할 수 없는 주문 상태입니다."),
    ORDER_CANNOT_CONFIRM(HttpStatus.BAD_REQUEST, "구매 확정할 수 없는 주문 상태입니다."),
    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "주문 수량은 1 이상이어야 합니다."),
    INVALID_PRODUCT_PRICE(HttpStatus.BAD_REQUEST, "상품 가격이 유효하지 않습니다."),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "주문자 정보가 유효하지 않습니다."),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "필수 입력 항목이 누락되었습니다."),

    // ===== 타임딜 관련 =====
    TIME_DEAL_NOT_FOUND(HttpStatus.NOT_FOUND, "타임딜을 찾을 수 없습니다."),
    TIME_DEAL_EXPIRED(HttpStatus.BAD_REQUEST, "타임딜 기간이 만료되었습니다."),
    TIME_DEAL_NOT_STARTED(HttpStatus.BAD_REQUEST, "타임딜이 아직 시작되지 않았습니다."),
    TIME_DEAL_SOLD_OUT(HttpStatus.CONFLICT, "타임딜 수량이 모두 소진되었습니다."),
    TIME_DEAL_INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "타임딜 재고가 부족합니다."),
    INVALID_TIME_DEAL(HttpStatus.BAD_REQUEST, "유효하지 않은 타임딜입니다."),

    // ==== 쿠폰 관련 ====
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠폰을 찾을 수 없습니다."),
    COUPON_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "사용할 수 없는 쿠폰입니다."),
    COUPON_ALREADY_USED(HttpStatus.BAD_REQUEST, "이미 사용된 쿠폰입니다."),
    COUPON_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 쿠폰입니다."),
    COUPON_MIN_AMOUNT_NOT_MET(HttpStatus.BAD_REQUEST, "쿠폰 사용 최소 금액을 충족하지 않습니다."),
    INVALID_COUPON(HttpStatus.BAD_REQUEST, "유효하지 않은 쿠폰입니다."),
    COUPON_USE_FAILED(HttpStatus.BAD_REQUEST, "쿠폰 사용에 실패했습니다."),
    INVALID_COUPON_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 쿠폰 타입입니다."),
    INVALID_COUPON_VALUE(HttpStatus.BAD_REQUEST, "쿠폰 할인 값이 올바르지 않습니다."),

    // ==== 상품 관련 =====
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    PRODUCT_UNAVAILABLE(HttpStatus.BAD_REQUEST, "판매 중단된 상품입니다."),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "상품 재고가 부족합니다."),
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "품절된 상품입니다."),

    // ===== 배송 관련 =====
    INVALID_DELIVERY_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "배송 상태를 변경할 수 없습니다."),
    DELIVERY_ALREADY_STARTED(HttpStatus.BAD_REQUEST, "이미 배송이 시작된 주문입니다."),
    DELIVERY_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "배송이 완료되지 않은 주문입니다."),
    DELIVERY_INFO_NOT_EDITABLE(HttpStatus.BAD_REQUEST, "배송 정보를 수정할 수 없는 상태입니다."),
    INVALID_RECIPIENT_INFO(HttpStatus.BAD_REQUEST, "수령인 정보가 유효하지 않습니다."),
    INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "배송 주소가 유효하지 않습니다."),
    INVALID_TRACKING_NUMBER(HttpStatus.BAD_REQUEST, "운송장 번호가 유효하지 않습니다."),

    // ===== 결제 관련 =====
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "결제에 실패했습니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "결제 금액이 일치하지 않습니다."),
    PAYMENT_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "결제 시간이 초과되었습니다."),

    // ===== 권한 관련 =====
    USER_NOT_ALLOWED(HttpStatus.FORBIDDEN, "접근 권한이 없는 사용자입니다."),
    ORDER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "주문에 접근할 권한이 없습니다."),
    ORDER_MODIFICATION_DENIED(HttpStatus.FORBIDDEN, "주문을 수정할 권한이 없습니다."),

    // ===== 외부 서비스 연동 오류 =====
    PRODUCT_SERVICE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "상품 서비스 연동 중 오류가 발생했습니다."),
    COUPON_SERVICE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "쿠폰 서비스 연동 중 오류가 발생했습니다."),
    USER_SERVICE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "사용자 서비스 연동 중 오류가 발생했습니다."),
    TIME_DEAL_SERVICE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "타임딜 서비스 연동 중 오류가 발생했습니다."),
    PAYMENT_SERVICE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "결제 서비스 연동 중 오류가 발생했습니다."),

    // ===== 서버 내부 오류 =====
    ORDER_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "주문 생성 중 오류가 발생했습니다."),
    ORDER_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "주문 수정 중 오류가 발생했습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}