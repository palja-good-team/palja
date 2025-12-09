package com.palja.timedeal_service.application.command;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RestoreRemainingQuantityCommand(
        UUID timeDealId,
        long restoreQuantity
) {}
