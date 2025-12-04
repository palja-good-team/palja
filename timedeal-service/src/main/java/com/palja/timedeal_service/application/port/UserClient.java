package com.palja.timedeal_service.application.port;

import com.palja.timedeal_service.application.dto.external.CompanyUserInfo;

public interface UserClient {
    CompanyUserInfo getCompanyUserByLoginId(String loginId);
}
