package com.palja.payment_service.application.service;

import com.palja.payment_service.application.dto.response.UserRes;

public interface UserService {
    UserRes getUserByLoginId(String loginId);
}
