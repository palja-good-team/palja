package com.palja.timedeal_service.application.validator;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.common.vo.UserRole;
import com.palja.timedeal_service.application.dto.external.CompanyUserInfo;
import com.palja.timedeal_service.application.port.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorityValidator {

    private final UserClient userClient;

    public UUID verifyCompanyUserId(String loginId, UserRole role, UUID ownerCompanyUserId) {
        if (UserRole.CUSTOMER.equals(role)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }

        if (UserRole.MANAGER.equals(role)) {
            return ownerCompanyUserId;
        }

        if (UserRole.COMPANY_USER.equals(role)) {
            CompanyUserInfo companyUserInfo = userClient.getCompanyUserByLoginId(loginId);

            if (!companyUserInfo.companyUserId().equals(ownerCompanyUserId)) {
                throw new BusinessException(CommonErrorCode.FORBIDDEN);
            }

            return companyUserInfo.companyUserId();
        }

        throw new BusinessException(CommonErrorCode.FORBIDDEN);
    }
}
