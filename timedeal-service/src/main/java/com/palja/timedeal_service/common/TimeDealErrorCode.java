package com.palja.timedeal_service.common;

import com.palja.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TimeDealErrorCode implements ErrorCode {

    // 필수 값 관련
    PRODUCT_ID_REQUIRED(HttpStatus.BAD_REQUEST, "상품 ID는 null일 수 없습니다."),
    COMPANY_USER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "업체 관리자 ID는 null일 수 없습니다."),
    TITLE_REQUIRED(HttpStatus.BAD_REQUEST, "타임딜 제목은 비어 있을 수 없습니다."),
    DESCRIPTION_REQUIRED(HttpStatus.BAD_REQUEST, "타임딜 설명은 비어 있을 수 없습니다."),

    // 기간 관련
    PERIOD_REQUIRED(HttpStatus.BAD_REQUEST, "시작 시간과 종료 시간은 null일 수 없습니다."),
    PERIOD_START_TIME_INVALID(HttpStatus.BAD_REQUEST, "시작 시간은 현재 시간 이후여야 합니다."),
    PERIOD_END_BEFORE_START(HttpStatus.BAD_REQUEST, "종료 시간은 시작 시간보다 이후여야 합니다."),

    // 가격 관련
    INVALID_ORIGINAL_PRICE(HttpStatus.BAD_REQUEST, "원래 가격은 0보다 커야 합니다."),
    INVALID_TIMEDEAL_PRICE(HttpStatus.BAD_REQUEST, "타임딜 가격은 0보다 커야 합니다."),
    TIMEDEAL_PRICE_GREATER_THAN_ORIGINAL(HttpStatus.BAD_REQUEST, "타임딜 가격은 원래 가격보다 클 수 없습니다."),

    // 재고 관련
    INVALID_TOTAL_QUANTITY(HttpStatus.BAD_REQUEST, "타임딜 재고는 0보다 커야 합니다."),
    INVALID_STOCK_QUANTITY(HttpStatus.BAD_REQUEST, "타임딜 재고는 상품의 실제 재고를 초과할 수 없습니다."),
    INVALID_TOTAL_QUANTITY_UPDATE(HttpStatus.BAD_REQUEST, "이미 판매된 수량보다 적은 총 수량으로 변경할 수 없습니다."),
    INVALID_REMAINING_QUANTITY(HttpStatus.BAD_REQUEST, "남은 수량이 총 수량 범위를 벗어났습니다."),
    TIME_DEAL_INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "잘못된 수량이 요청되었습니다."),
    TIME_DEAL_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "요청 수량만큼 재고가 남아있지 않습니다."),
    TIME_DEAL_PENDING_CANNOT_RESTORE(HttpStatus.BAD_REQUEST, "PENDING 상태의 타임딜은 재고를 복구할 수 없습니다."),
    TIME_DEAL_CLOSED_RESTORE_EXTERNAL(HttpStatus.BAD_REQUEST, "CLOSED 상태의 타임딜은 내부 재고를 복구할 수 없으며, 외부 상품 재고를 복구해야 합니다."),
    TIME_DEAL_STOCK_OVERFLOW(HttpStatus.BAD_REQUEST, "복구된 재고가 총 재고 수량을 초과할 수 없습니다."),

    // 수정 관련
    TIME_DEAL_NOT_EDITABLE(HttpStatus.BAD_REQUEST, "타임딜을 수정할 수 없는 상태입니다."),
    INVALID_TIME_DEAL_STATUS(HttpStatus.BAD_REQUEST, "알맞지 않은 타임딜 상태입니다."),
    TIME_DEAL_STATUS_REQUIRED(HttpStatus.BAD_REQUEST, "변경할 타임딜 상태는 필수입니다."),
    TIME_DEAL_STATUS_ALREADY_APPLIED(HttpStatus.BAD_REQUEST, "현재 상태와 동일한 상태로는 변경할 수 없습니다."),
    TIME_DEAL_INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "허용되지 않은 상태 변경입니다."),
    TIME_DEAL_STATUS_REASON_REQUIRED(HttpStatus.BAD_REQUEST, "상태 변경 사유는 필수입니다."),

    // 외부 관련
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    COMPANY_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "업체 판매자를 찾을 수 없습니다."),
    TIME_DEAL_NOT_OPEN(HttpStatus.BAD_REQUEST, "OPEN 상태의 타임딜만 재고 차감이 가능합니다."),
    TIME_DEAL_NOT_IN_PERIOD(HttpStatus.BAD_REQUEST, "현재 시간에 재고 차감을 수행할 수 없는 타임딜입니다.");

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
