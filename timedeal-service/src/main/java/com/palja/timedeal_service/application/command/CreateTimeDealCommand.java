package com.palja.timedeal_service.application.command;

import com.palja.common.vo.UserRole;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CreateTimeDealCommand (
        UUID productId,
        String title,
        String description,
        LocalDateTime startAt,
        LocalDateTime endAt,
        long timeDealPrice,
        long totalQuantity,
        UserRole role
) {}
