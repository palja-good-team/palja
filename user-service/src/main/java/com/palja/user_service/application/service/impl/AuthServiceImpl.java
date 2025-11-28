package com.palja.user_service.application.service.impl;

import org.springframework.stereotype.Service;

import com.palja.user_service.application.service.AuthService;
import com.palja.user_service.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;

}
