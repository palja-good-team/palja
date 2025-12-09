package com.palja.order_service.infrastructure.external.adapter;

import com.palja.order_service.application.dto.response.CompanyUserRes;
import com.palja.order_service.application.dto.response.CustomerUserRes;
import com.palja.order_service.application.dto.response.ManagerUserRes;
import com.palja.order_service.application.service.UserService;
import com.palja.order_service.infrastructure.external.dto.response.CompanyUserDTO;
import com.palja.order_service.infrastructure.external.dto.response.CustomerUserDTO;
import com.palja.order_service.infrastructure.external.dto.response.ManagerUserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserAdapter implements UserService {

    // TODO: 유저 서비스 연동 시 userClient 주입 및 구현 추가
    //private final UserClient userClient;

    @Override
    public CustomerUserRes getCustomerUserByLoginId(String loginId) {
        log.debug("고객 사용자 조회 요청: loginId={}", loginId);

        // TODO: user-service 연동 시 FeignClient 호출
        // CustomerUserDTO dto = userClient.getCustomerUserByLoginId(loginId).getData();
        // TODO: 실제 유저 서비스 연동 시 위의 코드로 교체
        // 임시 더미 데이터
        CustomerUserDTO response = CustomerUserDTO.dummy(loginId);

        return CustomerUserRes.of(
                response.getUserId(),
                response.getLoginId(),
                response.getName(),
                response.getEmail(),
                response.getRole(),
                response.getStatus()
        );
    }

    @Override
    public ManagerUserRes getManagerUserByLoginId(String loginId) {
        log.debug("MANAGER 사용자 조회 요청: loginId={}", loginId);

        // TODO: user-service 연동 시 FeignClient 호출
        // ManagerUserDTO dto = userClient.getManagerUserByLoginId(loginId).getData();
        // TODO: 실제 유저 서비스 연동 시 위의 코드로 교체
        // 임시 더미 데이터
        ManagerUserDTO dto = ManagerUserDTO.dummy(loginId);

        return ManagerUserRes.of(
                dto.getUserId(),
                dto.getLoginId(),
                dto.getName(),
                dto.getEmail(),
                dto.getRole(),
                dto.getStatus()
        );
    }

    @Override
    public CompanyUserRes getCompanyUserByLoginId(String loginId) {
        log.debug("판매업체 사용자 조회 요청: loginId={}", loginId);

        // TODO: user-service 연동 시 FeignClient 호출
        // CompanyUserDTO dto = userClient.getCompanyUserByLoginId(loginId).getData();
        // TODO: 실제 유저 서비스 연동 시 위의 코드로 교체
        // 임시 더미 데이터
        CompanyUserDTO dto = CompanyUserDTO.dummy(loginId);

        return CompanyUserRes.of(
                dto.getUserId(),
                dto.getCompanyUserId(),
                dto.getLoginId(),
                dto.getCompanyName(),
                dto.getEmail(),
                dto.getRole(),
                dto.getStatus()
        );
    }
}