package com.palja.timedeal_service.application.command;

import com.palja.common.vo.UserRole;

import java.util.UUID;

public record DeleteTimeDealCommand (
        UUID timeDealId,
        UserRole role
) {
    public static DeleteTimeDealCommand of(UUID timeDealId, UserRole role) {
        return new DeleteTimeDealCommand(timeDealId, role);
    }
}
