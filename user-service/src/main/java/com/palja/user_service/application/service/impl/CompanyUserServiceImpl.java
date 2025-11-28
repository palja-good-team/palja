package com.palja.user_service.application.service.impl;

import org.springframework.stereotype.Service;

import com.palja.user_service.application.service.CompanyUserService;
import com.palja.user_service.domain.repository.CompanyUserRepository;
import com.palja.user_service.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyUserServiceImpl implements CompanyUserService {

	private final CompanyUserRepository companyUserRepository;
	private final UserRepository userRepository;

}
