package com.palja.product_service.application.port;

import com.palja.product_service.application.dto.external.CompanyUserInfoRes;

public interface UserClient {

    CompanyUserInfoRes getMyInfo();
}
