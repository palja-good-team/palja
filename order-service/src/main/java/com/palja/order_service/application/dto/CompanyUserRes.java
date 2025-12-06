package com.palja.order_service.application.dto;

import com.palja.common.vo.UserRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompanyUserRes {

    private final Long userId;
    private final UUID companyUserId;
    private final String loginId;
    private final String companyName;
    private final String email;
    private final UserRole role;
    private final String status;

    public static CompanyUserRes of(
            Long userId,
            UUID companyUserId,
            String loginId,
            String companyName,
            String email,
            UserRole role,
            String status
    ) {
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