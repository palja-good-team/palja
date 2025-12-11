package com.palja.user_service.application.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.palja.common.auditor.AuditorContext;
import com.palja.common.auditor.CurrentUser;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

	@Around("@within(org.springframework.web.bind.annotation.RestController)")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		String loginId = AuditorContext.get() != null ? CurrentUser.getLoginId() : "ANONYMOUS";

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
