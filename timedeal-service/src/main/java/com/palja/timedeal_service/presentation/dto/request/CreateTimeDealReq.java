package com.palja.timedeal_service.presentation.dto.request;

import com.palja.common.vo.UserRole;
import com.palja.timedeal_service.application.command.CreateTimeDealCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateTimeDealReq {
    @NotNull(message = "상품 ID는 필수입니다.")
    UUID productId;

    @NotNull(message = "업체 ID는 필수입니다.")
    UUID companyUserId;

    @NotBlank(message = "타임딜 제목은 필수입니다.")
    @Size(max = 100, message = "타임딜 제목은 최대 100자까지 입력 가능합니다.")
    String title;

    @NotBlank(message = "타임딜 설명은 필수입니다.")
    @Size(max = 500, message = "타임딜 설명은 최대 500자까지 입력 가능합니다.")
    String description;

    @NotNull(message = "타임딜 시작 시간은 필수입니다.")
    LocalDateTime startAt;

    @NotNull(message = "타임딜 종료 시간은 필수입니다.")
    LocalDateTime endAt;

    @NotNull(message = "타임딜 가격은 필수입니다.")
    @Positive(message = "타임딜 가격은 0보다 커야 합니다.")
    long timeDealPrice;

    @NotNull(message = "타임딜 재고의 전체 수량은 필수입니다.")
    @Positive(message = "타임딜 재고의 전체 수량은 0보다 커야 합니다.")
    long totalQuantity;

    public CreateTimeDealCommand toCommand(String loginId, UserRole role) {
        return CreateTimeDealCommand.builder()
                .productId(productId)
                .companyUserId(companyUserId)
                .title(title)
                .description(description)
                .startAt(startAt)
                .endAt(endAt)
                .timeDealPrice(timeDealPrice)
                .totalQuantity(totalQuantity)
                .loginId(loginId)
                .role(role)
                .build();
    }
}
