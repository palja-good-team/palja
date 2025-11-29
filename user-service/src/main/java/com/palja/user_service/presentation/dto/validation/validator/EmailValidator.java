package com.palja.user_service.presentation.dto.validation.validator;

import com.palja.user_service.presentation.dto.validation.annotation.ValidEmail;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isBlank()) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("이메일을 입력해주세요.")
				.addConstraintViolation();

			return false;
		}

		if (!value.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("이메일 형식이 올바르지 않습니다.")
				.addConstraintViolation();

			return false;
		}

		return true;
	}

}
