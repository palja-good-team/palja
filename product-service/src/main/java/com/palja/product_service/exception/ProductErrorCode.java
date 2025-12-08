package com.palja.product_service.exception;

import com.palja.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    INVALID_PRODUCT_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),
    INVALID_PRICE(HttpStatus.BAD_REQUEST, "가격은 양수여야 합니다."),
    INVALID_STOCK(HttpStatus.BAD_REQUEST, "재고는 음수일 수 없습니다."),
    NOT_SUPPORT_CATEGORY(HttpStatus.BAD_REQUEST, "지원되지 않는 카테고리입니다."),
    NAME_TOO_LONG(HttpStatus.BAD_REQUEST, "상품의 이름은 최대 30자까지 입니다."),

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."),
    DUPLICATE_PRODUCT(HttpStatus.BAD_REQUEST, "중복된 상품을 등록할 수 없습니다."),

    CONNECTION_ERROR_REDIS(HttpStatus.SERVICE_UNAVAILABLE, "요청이 많아 혼잡하니 다시 시도해주세요");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
