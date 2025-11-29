package com.palja.user_service.presentation.dto.validation.validator;

import com.palja.user_service.presentation.dto.validation.annotation.ValidCompanyNumber;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CompanyNumberValidator implements ConstraintValidator<ValidCompanyNumber, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isBlank()) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("사업자 등록 번호를 입력해주세요.")
				.addConstraintViolation();

			return false;
		}

		if (!value.matches("^[0-9]{3}-[0-9]{2}-[0-9]{5}$")) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("사업자 등록 번호 형식이 올바르지 않습니다.")
				.addConstraintViolation();

			return false;
		}

		return true;
	}

}
