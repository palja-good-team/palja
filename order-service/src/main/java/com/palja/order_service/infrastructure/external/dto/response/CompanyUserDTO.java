package com.palja.order_service.infrastructure.external.dto.response;

import com.palja.common.vo.UserRole;
import com.palja.order_service.application.dto.external.CompanyUserRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUserDTO {

    private Long userId;
    private UUID companyUserId;
    private String loginId;
    private String name;
    private String companyName;
    private String companyNumber;
    private String email;
    private String address;
    private UserRole role;
    private String status;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public CompanyUserRes toResponse() {
        return CompanyUserRes.builder()
                .userId(userId)
                .companyUserId(companyUserId)
                .loginId(loginId)
                .companyName(companyName)
                .email(email)
                .role(role)
                .status(status)
                .build();
    }
}