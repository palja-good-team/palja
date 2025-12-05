package com.palja.timedeal_service.application.command;

import com.palja.common.vo.UserRole;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UpdateTimeDealCommand(
        UUID timeDealId,
        String title,
        String description,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Long timeDealPrice,
        Long totalQuantity,
        String loginId,
        UserRole role
) { }
