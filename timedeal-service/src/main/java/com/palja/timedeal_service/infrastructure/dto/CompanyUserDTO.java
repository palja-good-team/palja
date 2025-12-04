package com.palja.timedeal_service.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.palja.timedeal_service.application.dto.external.CompanyUserInfo;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CompanyUserDTO(
        UUID companyUserId
) {
    public CompanyUserInfo toInfo() {
        return new CompanyUserInfo(companyUserId);
    }
}