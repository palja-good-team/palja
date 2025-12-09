package com.palja.user_service.infrastructure.external.feign.adapter;

import org.springframework.stereotype.Component;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.common.response.ApiResponse;
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
			getDummy(userId); // TODO: API 개발 완료 후 실제 호출로 변경
			// reviewClient.deleteAllReviews(userId);
		} catch (FeignException e) {
			log.error("[Feign] status={} url=[{}] {} message={}",
				e.status(), e.request().httpMethod().name(), e.request().url(), e.contentUTF8());
			throw new BusinessException(CommonErrorCode.FEIGN_ERROR);
		}
	}

	private ApiResponse<Void> getDummy(Long userId) {
		return ApiResponse.success("리뷰가 삭제되었습니다.");
	}

}
