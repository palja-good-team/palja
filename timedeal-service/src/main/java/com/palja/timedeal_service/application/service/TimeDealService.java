package com.palja.timedeal_service.application.service;

import com.palja.timedeal_service.application.command.CreateTimeDealCommand;
import com.palja.timedeal_service.application.dto.TimeDealDetailRes;

import java.util.UUID;

public interface TimeDealService {
    TimeDealDetailRes createTimeDeal(CreateTimeDealCommand command);
    TimeDealDetailRes getTimeDeal(UUID timeDealId);
}
