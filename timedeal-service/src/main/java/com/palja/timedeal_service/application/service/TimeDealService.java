package com.palja.timedeal_service.application.service;

import com.palja.common.response.PageResponse;
import com.palja.timedeal_service.application.command.*;
import com.palja.timedeal_service.application.dto.TimeDealCreateRes;
import com.palja.timedeal_service.application.dto.TimeDealDetailRes;
import com.palja.timedeal_service.application.dto.TimeDealStatusChangeRes;
import com.palja.timedeal_service.application.dto.TimeDealUpdateRes;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TimeDealService {
    TimeDealCreateRes createTimeDeal(CreateTimeDealCommand command);
    TimeDealDetailRes getTimeDeal(UUID timeDealId);
    PageResponse<TimeDealDetailRes> getTimeDeals(Pageable pageable);
    TimeDealUpdateRes updateTimeDeal(UpdateTimeDealCommand command);
    TimeDealStatusChangeRes changeTimeDealStatus(ChangeTimeDealStatusCommand command);
    void deleteTimeDeal(DeleteTimeDealCommand command);
    void decreaseRemainingQuantity(DecreaseRemainingQuantityCommand command);
    void restoreRemainingQuantity(RestoreRemainingQuantityCommand command);
    void deleteByCompanyUser(UUID companyUserId);
    void changeTimeDealStatusFailed(ChangeTimeDealStatusFailedCommand command);
    void updateProductPrice(UpdateProductPriceCommand command);
}
