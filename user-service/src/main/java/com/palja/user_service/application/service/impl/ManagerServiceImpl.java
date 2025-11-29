package com.palja.user_service.application.service.impl;

import org.springframework.stereotype.Service;

import com.palja.user_service.application.service.ManagerService;
import com.palja.user_service.domain.repository.ManagerRepository;
import com.palja.user_service.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {

	private final ManagerRepository managerRepository;
	private final UserRepository userRepository;

}
