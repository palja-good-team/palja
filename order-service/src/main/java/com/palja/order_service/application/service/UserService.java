package com.palja.order_service.application.service;

import com.palja.order_service.application.dto.response.CompanyUserRes;
import com.palja.order_service.application.dto.response.CustomerUserRes;
import com.palja.order_service.application.dto.response.ManagerUserRes;

public interface UserService {

    // 고객 사용자 조회
    CustomerUserRes getCustomerUserByLoginId(String loginId);

    // 매니저 사용자 조회
    ManagerUserRes getManagerUserByLoginId(String loginId);

    // 판매업체 사용자 조회
    CompanyUserRes getCompanyUserByLoginId(String loginId);
}