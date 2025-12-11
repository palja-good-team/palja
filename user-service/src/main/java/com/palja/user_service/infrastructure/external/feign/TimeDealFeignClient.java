package com.palja.user_service.infrastructure.external.feign;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.palja.common.response.ApiResponse;

@FeignClient(name = "timedeal-service", path = "/api/v1/time-deals")
public interface TimeDealFeignClient {

	// TODO: 고도화 때 이벤트 기반 비동기 호출로 변경
	@DeleteMapping("/{companyUserId}")
	ApiResponse<Void> deleteAllTimeDeals(@PathVariable("companyUserId") UUID companyUserId);

}
