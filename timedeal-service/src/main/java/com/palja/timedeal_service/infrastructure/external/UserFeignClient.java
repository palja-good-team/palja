package com.palja.timedeal_service.infrastructure.external;

import com.palja.timedeal_service.infrastructure.external.dto.CompanyUserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "user-service", path = "/api/v1")
public interface UserFeignClient {

    @GetMapping("/company-users/{loginId}")
    public CompanyUserDTO getCompanyUserByLoginId(@PathVariable String loginId);
}
