package com.palja.user_service.infrastructure.external.feign.adapter;

import org.springframework.stereotype.Component;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.user_service.application.service.ReviewService;
import com.palja.user_service.infrastructure.external.feign.ReviewClient;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewAdapter implements ReviewService {

	private final ReviewClient reviewClient;

	@Override
	public void deleteAllReviews(Long userId) {
		try {
			reviewClient.deleteAllReviews(userId);
		} catch (FeignException e) {
			log.error("[Feign] status={} url=[{}] {} message={}",
				e.status(), e.request().httpMethod().name(), e.request().url(), e.contentUTF8());
			throw new BusinessException(CommonErrorCode.FEIGN_ERROR);
		}
	}

}
