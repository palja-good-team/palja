package com.palja.user_service.presentation.dto.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.palja.user_service.presentation.dto.validation.validator.EmailValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = EmailValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmail {

	String message() default "이메일 형식이 올바르지 않습니다.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
