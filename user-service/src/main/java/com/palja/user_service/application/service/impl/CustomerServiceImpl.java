package com.palja.user_service.application.service.impl;

import org.springframework.stereotype.Service;

import com.palja.user_service.application.service.CustomerService;
import com.palja.user_service.domain.repository.CustomerRepository;
import com.palja.user_service.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository customerRepository;
	private final UserRepository userRepository;

}
