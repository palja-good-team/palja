package com.palja.user_service.infrastructure.external.feign;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.palja.common.response.ApiResponse;

@FeignClient(name = "product-service", path = "/api/v1/products")
public interface ProductFeignClient {

	// TODO: 고도화 때 이벤트 기반 비동기 호출로 변경
	@DeleteMapping("/user/{companyUserId}")
	ApiResponse<Void> deleteAllProducts(@PathVariable("companyUserId") UUID companyUserId);

}
