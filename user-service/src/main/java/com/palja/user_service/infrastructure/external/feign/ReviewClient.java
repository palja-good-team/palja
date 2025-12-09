package com.palja.user_service.infrastructure.external.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.palja.common.response.ApiResponse;

@FeignClient(name = "review-service", path = "/api/v1/reviews")
public interface ReviewClient {

	// TODO: 고도화 때 이벤트 기반 비동기 호출로 변경
	@DeleteMapping("/{userId}")
	ApiResponse<Void> deleteAllReviews(@PathVariable("userId") Long userId);

}
