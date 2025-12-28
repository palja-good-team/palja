package com.palja.timedeal_service.infrastructure.external.feign;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.common.response.ApiResponse;
import com.palja.timedeal_service.application.dto.external.CompanyUserInfo;
import com.palja.timedeal_service.application.port.UserClient;
import com.palja.timedeal_service.common.TimeDealErrorCode;
import com.palja.timedeal_service.infrastructure.external.feign.client.UserFeignClient;
import com.palja.timedeal_service.infrastructure.external.feign.dto.CompanyUserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserClientAdapter implements UserClient {

    private final UserFeignClient userFeignClient;

    @Override
    public CompanyUserInfo getCompanyUserByLoginId() {
        log.info("업체 판매자 정보 요청");

        try {
            ApiResponse<CompanyUserDTO> companyUser = userFeignClient.getCompanyUserByLoginId();
/*            //mock
            CompanyUserDTO companyUser = mockUser();*/

            if (companyUser == null) {
                log.error("[UserClient] 업체 사용자 없음");
                throw new BusinessException(TimeDealErrorCode.COMPANY_USER_NOT_FOUND);
            }

            log.info("업체 판매자 정보 요청 성공");
            return companyUser.data().toInfo();
        } catch (Exception e) {
            log.error("[UserClient] 업체 사용자 조회 실패: {}", e.getMessage());
            throw new BusinessException(CommonErrorCode.FEIGN_ERROR);
        }
    }

/*    private CompanyUserDTO mockUser() {
        log.info("[MockUserClient] 업체 사용자 Mock 반환");

        return new CompanyUserDTO(
                UUID.fromString("93cdf98a-60a4-4677-9474-4a3e7ecec284")
        );
    }*/
}
