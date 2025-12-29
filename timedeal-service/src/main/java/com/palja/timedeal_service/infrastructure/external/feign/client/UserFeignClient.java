package com.palja.timedeal_service.infrastructure.external.feign.client;

import com.palja.common.response.ApiResponse;
import com.palja.timedeal_service.infrastructure.external.feign.dto.CompanyUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service", path = "/api/v1")
public interface UserFeignClient {

    @GetMapping("/company-users/me")
    ApiResponse<CompanyUserDTO> getCompanyUserByLoginId();
}
