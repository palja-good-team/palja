package com.palja.timedeal_service.application.service;

import com.palja.common.response.PageResponse;
import com.palja.timedeal_service.application.command.*;
import com.palja.timedeal_service.application.dto.TimeDealDetailRes;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TimeDealService {
    TimeDealDetailRes createTimeDeal(CreateTimeDealCommand command);
    TimeDealDetailRes getTimeDeal(UUID timeDealId);
    PageResponse<TimeDealDetailRes> getTimeDeals(Pageable pageable);
    TimeDealDetailRes updateTimeDeal(UpdateTimeDealCommand command);
    TimeDealDetailRes changeTimeDealStatus(ChangeTimeDealStatusCommand command);
    void deleteTimeDeal(DeleteTimeDealCommand command);
    void decreaseRemainingQuantity(DecreaseRemainingQuantityCommand command);
    void restoreRemainingQuantity(RestoreRemainingQuantityCommand command);
}
