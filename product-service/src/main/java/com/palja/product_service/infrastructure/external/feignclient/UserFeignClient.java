package com.palja.product_service.infrastructure.external.feignclient;

import com.palja.common.response.ApiResponse;
import com.palja.product_service.infrastructure.dto.CompanyUserInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service")
public interface UserFeignClient {

    @GetMapping("/api/v1/company-users/me")
    ApiResponse<CompanyUserInfoDto> getMyInfo();
}
