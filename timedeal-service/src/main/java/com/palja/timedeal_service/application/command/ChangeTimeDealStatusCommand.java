package com.palja.timedeal_service.application.command;

import com.palja.common.vo.UserRole;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ChangeTimeDealStatusCommand(
        UUID timeDealId,
        String newStatus,
        String reason,
        String loginId,
        UserRole role
) {}
