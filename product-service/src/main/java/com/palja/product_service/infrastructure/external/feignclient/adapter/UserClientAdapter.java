package com.palja.product_service.infrastructure.external.feignclient.adapter;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.common.response.ApiResponse;
import com.palja.product_service.application.dto.external.CompanyUserInfoRes;
import com.palja.product_service.application.port.UserClient;
import com.palja.product_service.infrastructure.dto.CompanyUserInfoDto;
import com.palja.product_service.infrastructure.external.feignclient.UserFeignClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserClientAdapter implements UserClient {

    private final UserFeignClient userFeignClient;

    @Override
    public CompanyUserInfoRes getMyInfo() {

        try {
            ApiResponse<CompanyUserInfoDto> myInfo = userFeignClient.getMyInfo();
            return myInfo.data().toRes();
        } catch (FeignException fe) {
            log.info("UserServiceAdaptor getMyInfo FeignException");
            throw new BusinessException(CommonErrorCode.FEIGN_ERROR);
        }
    }
}
