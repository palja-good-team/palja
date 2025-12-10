package com.palja.product_service.infrastructure.external.adaptor;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.common.response.ApiResponse;
import com.palja.product_service.application.service.UserService;
import com.palja.product_service.infrastructure.dto.CompanyUserInfoDto;
import com.palja.product_service.infrastructure.external.UserFeignClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserServiceAdaptor implements UserService {

    private final UserFeignClient userFeignClient;

    @Override
    public CompanyUserInfoDto getMyInfo() {

        try {
            ApiResponse<CompanyUserInfoDto> myInfo = userFeignClient.getMyInfo();
            return myInfo.data();
        } catch (FeignException fe) {
            log.info("UserServiceAdaptor getMyInfo FeignException");
            throw new BusinessException(CommonErrorCode.FEIGN_ERROR);
        }
    }
}
