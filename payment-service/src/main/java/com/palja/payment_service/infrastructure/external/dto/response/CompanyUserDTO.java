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

    /*
     TODO: 유저 서비스 연동 전까지 사용하는 더미 데이터
           user-service 연결 후 삭제 예정
     */
    public static CompanyUserDTO dummy(String loginId) {
        return new CompanyUserDTO(
                UUID.fromString("93cdf98a-60a4-4677-9474-4a3e7ecec284"),
                1L,
                loginId,
                loginId,
                loginId + "@example.com",
                UserRole.COMPANY_USER,
                "ACTIVE"
        );
    }
}
