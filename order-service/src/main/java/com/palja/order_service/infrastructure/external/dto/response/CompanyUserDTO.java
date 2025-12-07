package com.palja.order_service.infrastructure.external.dto.response;

import com.palja.common.vo.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
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

    // TODO: 사용자 서비스 연동 전까지 사용하는 더미 데이터. user-service 연결 후 삭제.
    public static CompanyUserDTO dummy(String loginId) {
        return new CompanyUserDTO(
                100L,
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                loginId,
                "company",
                "업체",
                "123-45-67890",
                "company@gmail.com",
                "서울시 강남구 테헤란로 123",
                UserRole.COMPANY_USER,
                "PENDING",
                LocalDateTime.now(),
                loginId,
                LocalDateTime.now(),
                loginId
        );
    }
}