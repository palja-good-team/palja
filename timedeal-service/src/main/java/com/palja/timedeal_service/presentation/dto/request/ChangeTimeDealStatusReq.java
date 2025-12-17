package com.palja.timedeal_service.presentation.dto.request;

import com.palja.common.vo.UserRole;
import com.palja.timedeal_service.application.command.ChangeTimeDealStatusCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ChangeTimeDealStatusReq {
    @NotBlank(message = "새로운 상태 값은 필수입니다.")
    String newStatus;

    @NotBlank(message = "상태 변경 이유는 필수입니다.")
    @Size(max = 100, message = "상태 변경 이유는 최대 100자까지 입력 가능합니다.")
    String reason;

    public ChangeTimeDealStatusCommand toCommand(UUID timeDealId, UserRole role) {
        return ChangeTimeDealStatusCommand.builder()
                .timeDealId(timeDealId)
                .newStatus(newStatus)
                .reason(reason)
                .role(role)
                .build();
    }
}
