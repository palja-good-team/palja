package com.palja.user_service.presentation.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

	@Around("@within(org.springframework.web.bind.annotation.RestController)")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		String loginId = request.getHeader("X-USER-LOGIN-ID");
		if (loginId == null) loginId = "ANONYMOUS";

		String className = pjp.getSignature().getDeclaringType().getSimpleName();
		String methodName = pjp.getSignature().getName();
		long startTime = System.currentTimeMillis();
		log.info("[{}] {} 사용자가 {}을/를 요청했습니다.", className, loginId , methodName);

		Object result = pjp.proceed();

		long endTime = System.currentTimeMillis();
		log.info("요청 완료까지 {}ms가 소요됐습니다.", endTime - startTime);

		return result;
	}

}
