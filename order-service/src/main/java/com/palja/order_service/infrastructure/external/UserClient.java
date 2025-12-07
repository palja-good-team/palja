package com.palja.order_service.infrastructure.external;

import com.palja.common.response.ApiResponse;
import com.palja.order_service.infrastructure.external.dto.response.CompanyUserDTO;
import com.palja.order_service.infrastructure.external.dto.response.CustomerUserDTO;
import com.palja.order_service.infrastructure.external.dto.response.ManagerUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/api/v1")
public interface UserClient {

    // customer 사용자 정보 조회
    @GetMapping("/customers/{loginId}")
    ApiResponse<CustomerUserDTO> getCustomerUserByLoginId(@PathVariable("loginId") String loginId);

    // manager 사용자 정보 조회
    @GetMapping("/managers/{loginId}")
    ApiResponse<ManagerUserDTO> getManagerUserByLoginId(@PathVariable("loginId") String loginId);

    // company-user 사용자 정보 조회
    @GetMapping("/company-users/{loginId}")
    ApiResponse<CompanyUserDTO> getCompanyUserByLoginId(@PathVariable("loginId") String loginId);
}