package com.palja.user_service.application.exception;

import org.springframework.http.HttpStatus;

import com.palja.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
	DUPLICATED_LOGIN_ID(HttpStatus.CONFLICT, "이미 존재하는 로그인 아이디입니다."),
	DUPLICATED_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
	USER_STATUS_NOT_FOUND(HttpStatus.BAD_REQUEST, "유효하지 않은 상태 값입니다."),
	;

	private final HttpStatus httpStatus;
	private final String message;

}
