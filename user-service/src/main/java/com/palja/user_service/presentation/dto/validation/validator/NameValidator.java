package com.palja.user_service.presentation.dto.validation.validator;

import com.palja.user_service.presentation.dto.validation.annotation.ValidName;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameValidator implements ConstraintValidator<ValidName, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isBlank()) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("이름을 입력해주세요.")
				.addConstraintViolation();

			return false;
		}

		if (!value.matches("^[가-힣A-Za-z0-9]{2,10}$")) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("이름은 2~10자의 한글, 알파벳, 숫자만 입력 가능합니다.")
				.addConstraintViolation();

			return false;
		}

		return true;
	}

}
