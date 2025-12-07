package com.palja.timedeal_service.application.command;

import lombok.Builder;

import java.util.UUID;

@Builder
public record DecreaseRemainingQuantityCommand(
        UUID timeDealId,
        long deltaQuantity
) {
}
