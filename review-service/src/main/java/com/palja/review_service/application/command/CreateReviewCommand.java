package com.palja.review_service.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateReviewCommand(
        String title,
        String content,
        BigDecimal rating,
        Boolean isLike,
        Boolean disLike,
        UUID orderId,
        String loginId
) {
}
