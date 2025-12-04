package com.palja.timedeal_service.infrastructure.external.adapter;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.timedeal_service.application.dto.external.CompanyUserInfo;
import com.palja.timedeal_service.application.port.UserClient;
import com.palja.timedeal_service.common.TimeDealErrorCode;
import com.palja.timedeal_service.infrastructure.dto.CompanyUserDTO;
import com.palja.timedeal_service.infrastructure.external.UserFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserClientImpl implements UserClient {

//    private final UserFeignClient userFeignClient;

    @Override
    public CompanyUserInfo getCompanyUserByLoginId(String loginId) {
        log.info("업체 판매자 정보 요청: loginId = {}", loginId);

        try {
/*        // TODO. 추후 변경
        CompanyUserDTO companyUser = userFeignClient.getCompanyUserByLoginId(loginId);*/
            CompanyUserDTO companyUser = mockUser(loginId);

            if (companyUser == null) {
                log.error("[UserClient] 업체 사용자 없음: loginId={}", loginId);
                throw new BusinessException(TimeDealErrorCode.COMPANY_USER_NOT_FOUND);
            }

            log.info("업체 판매자 정보 요청 성공");
            return companyUser.toInfo();
        } catch (Exception e) {
            log.error("[UserClient] 업체 사용자 조회 실패: {}", e.getMessage());
            throw new BusinessException(CommonErrorCode.FEIGN_ERROR);
        }
    }

    private CompanyUserDTO mockUser(String loginId) {
        log.info("[MockUserClient] 업체 사용자 Mock 반환");

        return new CompanyUserDTO(
                UUID.fromString("93cdf98a-60a4-4677-9474-4a3e7ecec284")
        );
    }
}
