package com.palja.product_service.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserInfo {

    private UUID companyUserId;
    private String companyName;
}
