package com.palja.timedeal_service.application.command;

import com.palja.common.vo.UserRole;

import java.util.UUID;

public record DeleteTimeDealCommand (
        UUID timeDealId,
        String loginId,
        UserRole role
) {
    public static DeleteTimeDealCommand of(UUID timeDealId, String loginId, UserRole role) {
        return new DeleteTimeDealCommand(timeDealId, loginId, role);
    }
}
