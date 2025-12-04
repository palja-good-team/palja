package com.palja.timedeal_service.application.dto.external;

import java.util.UUID;

public record ProductInfo(
        UUID productId,
        UUID companyUserId,
        long price,
        long stock
) {}