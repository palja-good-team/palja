package com.palja.user_service.application.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.user_service.application.command.CreateCustomerCommand;
import com.palja.user_service.application.service.CustomerService;
import com.palja.user_service.domain.entity.Customer;
import com.palja.user_service.domain.entity.User;
import com.palja.user_service.domain.repository.CustomerRepository;
import com.palja.user_service.domain.repository.UserRepository;
import com.palja.user_service.domain.vo.UserRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository customerRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void createCustomer(CreateCustomerCommand command) {
		validateDuplicateLoginId(command.loginId());
		validateDuplicateName(command.name());
		validateDuplicateEmail(command.email());

		User user = User.builder()
			.loginId(command.loginId())
			.password(passwordEncoder.encode(command.password()))
			.role(UserRole.CUSTOMER)
			.build();

		Customer customer = Customer.builder()
			.user(user)
			.name(command.name())
			.email(command.email())
			.address(command.address())
			.build();

		customerRepository.save(customer);
	}

	// TODO: 예외코드 생성
	private void validateDuplicateLoginId(String loginId) {
		if (userRepository.existsByLoginIdAndDeletedAtIsNull(loginId)) {
			throw new BusinessException(CommonErrorCode.DOMAIN_ERROR);
		}
	}

	// TODO: 중복 검사 전략 결정 후 작성
	private void validateDuplicateName(String name) {
	}

	private void validateDuplicateEmail(String email) {
		if (customerRepository.existsByEmailAndDeletedAtIsNull(email)) {
			throw new BusinessException(CommonErrorCode.DOMAIN_ERROR);
		}
	}

}
