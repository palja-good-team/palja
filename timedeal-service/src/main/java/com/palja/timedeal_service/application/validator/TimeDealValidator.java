package com.palja.timedeal_service.application.validator;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.common.vo.UserRole;
import com.palja.timedeal_service.application.command.CreateTimeDealCommand;
import com.palja.timedeal_service.application.dto.external.CompanyUserInfo;
import com.palja.timedeal_service.application.dto.external.ProductInfo;
import com.palja.timedeal_service.application.port.UserClient;
import com.palja.timedeal_service.common.TimeDealEditableField;
import com.palja.timedeal_service.common.TimeDealErrorCode;
import com.palja.timedeal_service.domain.entity.TimeDeal;
import com.palja.timedeal_service.domain.vo.TimeDealStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeDealValidator {

    private final UserClient userClient;

    public void validateCompanyUserId(UUID ownerCompanyUserId) {
            CompanyUserInfo companyUserInfo = userClient.getCompanyUserByLoginId();

            if (!companyUserInfo.companyUserId().equals(ownerCompanyUserId)) {
                throw new BusinessException(CommonErrorCode.FORBIDDEN);
            }
    }

    public void validateStock(long totalQuantity, long productStock) {
        if (productStock < totalQuantity) {
            throw new BusinessException(TimeDealErrorCode.INVALID_STOCK_QUANTITY);
        }
    }

    public void validateEditable(TimeDealStatus status, TimeDealEditableField field) {
        if (!status.canEditField(field)) {
            throw new BusinessException(TimeDealErrorCode.TIME_DEAL_NOT_EDITABLE);
        }
    }
}
