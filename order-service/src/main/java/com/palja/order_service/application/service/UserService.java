package com.palja.order_service.application.service;

import com.palja.order_service.application.dto.UserRes;

public interface UserService {

    UserRes getUserByLoginId(String loginId);
}