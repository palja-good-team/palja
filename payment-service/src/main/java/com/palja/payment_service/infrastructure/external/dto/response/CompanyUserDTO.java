package com.palja.payment_service.infrastructure.external.dto.response;

import com.palja.common.vo.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyUserDTO {
    private UUID companyUserId;
    private Long userId;
    private String loginId;
    private String name;
    private String email;
    private UserRole role;
    private String status;
}
