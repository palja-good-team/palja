package com.palja.timedeal_service.application.validator;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.common.vo.UserRole;
import com.palja.timedeal_service.application.command.CreateTimeDealCommand;
import com.palja.timedeal_service.application.dto.external.CompanyUserInfo;
import com.palja.timedeal_service.application.dto.external.ProductInfo;
import com.palja.timedeal_service.application.port.UserClient;
import com.palja.timedeal_service.common.TimeDealErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeDealValidator {

    private final UserClient userClient;

    public void verifyCompanyUserId(String loginId, UUID ownerCompanyUserId) {
            CompanyUserInfo companyUserInfo = userClient.getCompanyUserByLoginId(loginId);

            if (!companyUserInfo.companyUserId().equals(ownerCompanyUserId)) {
                throw new BusinessException(CommonErrorCode.FORBIDDEN);
            }
    }

    public void verifyStock(long totalQuantity, long productStock) {
        if (productStock < totalQuantity) {
            throw new BusinessException(TimeDealErrorCode.INVALID_STOCK_QUANTITY);
        }
    }
}
