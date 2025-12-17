package com.palja.order_service.infrastructure.external.client.dto.response;

import com.palja.common.vo.UserRole;
import com.palja.order_service.application.dto.external.ManagerUserRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ManagerUserDTO {

    private Long userId;
    private String loginId;
    private String name;
    private String email;
    private String address;
    private UserRole role;   // MASTER, MANAGER, CUSTOMER, COMPANY_USER
    private String status;   // PENDING, ACTIVE
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public ManagerUserRes toResponse() {
        return ManagerUserRes.builder()
                .userId(userId)
                .loginId(loginId)
                .name(name)
                .email(email)
                .role(role)
                .status(status)
                .build();
    }
}