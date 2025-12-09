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

    // customer: 회원 단건 조회 (본인)
    @GetMapping("/customers/me")
    ApiResponse<CustomerUserDTO> getMyCustomer();

    // company-user: 회원 단건 조회 (본인)
    @GetMapping("/company-users/me")
    ApiResponse<CompanyUserDTO> getMyCompanyUser();

    // manager: 회원 단건 조회 (본인)
    @GetMapping("/managers/me")
    ApiResponse<ManagerUserDTO> getMyManager();

}