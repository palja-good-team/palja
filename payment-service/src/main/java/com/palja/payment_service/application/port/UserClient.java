package com.palja.payment_service.application.port;

import com.palja.payment_service.application.dto.external.UserRes;

public interface UserClient {
    UserRes getUserByLoginId(String loginId);
    
    UserRes getUserByUserId(Long userId);
}
