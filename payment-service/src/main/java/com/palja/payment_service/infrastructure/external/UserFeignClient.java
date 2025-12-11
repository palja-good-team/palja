package com.palja.payment_service.infrastructure.external;

import com.palja.common.response.ApiResponse;
import com.palja.payment_service.infrastructure.external.dto.response.CompanyUserDTO;
import com.palja.payment_service.infrastructure.external.dto.response.CustomerUserDTO;
import com.palja.payment_service.infrastructure.external.dto.response.ManagerUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/api/v1")
public interface UserFeignClient {
    @GetMapping("/customers/{loginId}")
    ApiResponse<CustomerUserDTO> getCustomerUserByLoginId(@PathVariable("loginId") String loginId);

    @GetMapping("/managers/{loginId}")
    ApiResponse<ManagerUserDTO> getManagerUserByLoginId(@PathVariable("loginId") String loginId);

    @GetMapping("/company-users/{loginId}")
    ApiResponse<CompanyUserDTO> getCompanyUserByLoginId(@PathVariable("loginId") String loginId);

    @GetMapping("/customers/internal/{userId}")
    ApiResponse<CustomerUserDTO> getCustomerUserByUserId(@PathVariable("userId") Long userId);

    @GetMapping("/managers/internal/{userId}")
    ApiResponse<ManagerUserDTO> getManagerUserByUserId(@PathVariable("userId") Long userId);
}
