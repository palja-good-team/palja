package com.palja.order_service.application.port;

import com.palja.order_service.application.dto.external.CompanyUserRes;
import com.palja.order_service.application.dto.external.CustomerUserRes;
import com.palja.order_service.application.dto.external.ManagerUserRes;

public interface UserClient {

    // 고객 사용자 조회
    CustomerUserRes getMyCustomer(String loginId);

    // 판매업체 사용자 조회
    CompanyUserRes getMyCompanyUser(String loginId);

    // 매니저 사용자 조회
    ManagerUserRes getMyManager(String loginId);
}