package com.palja.user_service.presentation.dto.validation.validator;

import com.palja.user_service.presentation.dto.validation.annotation.ValidLoginId;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LoginIdValidator implements ConstraintValidator<ValidLoginId, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isBlank()) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("로그인 아이디를 입력해주세요.")
				.addConstraintViolation();

			return false;
		}

		if (!value.matches("^[a-z0-9]{4,10}$")) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("로그인 아이디는 4~10자의 알파벳 소문자와 숫자만 입력 가능합니다.")
				.addConstraintViolation();

			return false;
		}

		return true;
	}

}
