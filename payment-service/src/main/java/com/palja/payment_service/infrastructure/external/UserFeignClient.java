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

    @GetMapping("/customers/me")
    ApiResponse<CustomerUserDTO> getCustomerUserByLoginId();

    @GetMapping("/managers/me")
    ApiResponse<ManagerUserDTO> getManagerUserByLoginId();

    @GetMapping("/company-users/me")
    ApiResponse<CompanyUserDTO> getCompanyUserByLoginId();
}
