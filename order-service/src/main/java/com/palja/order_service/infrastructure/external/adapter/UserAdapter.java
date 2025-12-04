package com.palja.order_service.infrastructure.external.adapter;

import com.palja.order_service.application.dto.UserRes;
import com.palja.order_service.application.service.UserService;
import com.palja.order_service.infrastructure.external.dto.response.UserDTO;
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
    public UserRes getUserByLoginId(String loginId) {
        log.debug("사용자 조회 요청: loginId={}", loginId);

        // TODO: user-service 연동 시 FeignClient 호출 사용
        // UserDTO response = userClient.getUserByLoginId(loginId).data();
        // TODO: 실제 타임딜 서비스 연동 시 위의 코드로 교체
        // 임시 더미 데이터
        UserDTO response = UserDTO.dummy(loginId);

        return UserRes.from(response);
    }
}