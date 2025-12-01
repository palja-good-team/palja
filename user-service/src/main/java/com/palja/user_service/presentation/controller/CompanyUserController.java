package com.palja.user_service.presentation.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.palja.user_service.application.service.CompanyUserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/company-users")
@RequiredArgsConstructor
public class CompanyUserController {

	private final CompanyUserService companyUserService;

}
