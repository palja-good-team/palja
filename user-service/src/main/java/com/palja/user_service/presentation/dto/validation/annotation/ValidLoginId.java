package com.palja.user_service.presentation.dto.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.palja.user_service.presentation.dto.validation.validator.LoginIdValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = LoginIdValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidLoginId {

	String message () default "로그인 아이디 형식이 올바르지 않습니다.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
