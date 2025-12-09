package com.palja.user_service.application.exception;

import org.springframework.http.HttpStatus;

import com.palja.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

	INVALID_USER_INFO(HttpStatus.UNAUTHORIZED, "로그인 정보가 잘못되었습니다."),
	USER_STATUS_PENDING(HttpStatus.UNAUTHORIZED, "가입 승인 대기 중인 계정입니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "다시 로그인해주세요."),
	NOT_FOUND_TOKEN(HttpStatus.UNAUTHORIZED, "토큰을 찾을 수 없습니다."),
	;

	private final HttpStatus httpStatus;
	private final String message;

}
