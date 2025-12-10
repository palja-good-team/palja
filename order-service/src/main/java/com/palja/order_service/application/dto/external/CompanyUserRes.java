package com.palja.order_service.application.dto.external;

import com.palja.common.vo.UserRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompanyUserRes {

    private final Long userId;
    private final UUID companyUserId;
    private final String loginId;
    private final String companyName;
    private final String email;
    private final UserRole role;
    private final String status;
}