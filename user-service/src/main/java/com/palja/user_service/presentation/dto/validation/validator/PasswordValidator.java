package com.palja.user_service.presentation.dto.validation.validator;

import com.palja.user_service.presentation.dto.validation.annotation.ValidPassword;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
	// 최소 8자 이상, 15자 이하이며 알파벳 대소문자(a~z, A~Z), 숫자(0~9), 특수문자
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isBlank()) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("비밀번호를 입력해주세요.")
				.addConstraintViolation();

			return false;
		}

		if (!value.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).{8,15}$")) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("비밀번호는 8~15자로 알파벳 대문자, 소문자, 숫자, 특수문자를 모두 포함해야 합니다.")
				.addConstraintViolation();

			return false;
		}

		return true;
	}

}
