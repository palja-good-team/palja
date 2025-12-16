package com.palja.product_service.application.dto.external;

import com.palja.product_service.domain.dto.req.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CompanyUserInfoRes {

    private Long userId;
    private UUID companyUserId;
    private String loginId;
    private String name;
    private String companyName;
    private String companyNumber;
    private String email;
    private String address;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;

    public UserInfo toUserInfo() {
        return new UserInfo(companyUserId, companyName);
    }
}
