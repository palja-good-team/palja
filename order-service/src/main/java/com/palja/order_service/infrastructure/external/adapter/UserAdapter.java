package com.palja.order_service.infrastructure.external.adapter;

import com.palja.order_service.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAdapter implements UserService {

    // TODO: 유저 서비스 연동 시 userClient 주입 및 구현 추가
    //private final UserClient userClient;
}