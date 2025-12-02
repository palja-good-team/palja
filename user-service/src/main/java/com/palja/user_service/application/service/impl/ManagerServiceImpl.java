package com.palja.user_service.application.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.user_service.application.command.CreateManagerCommand;
import com.palja.user_service.application.service.ManagerService;
import com.palja.user_service.domain.entity.Manager;
import com.palja.user_service.domain.entity.User;
import com.palja.user_service.domain.repository.ManagerRepository;
import com.palja.user_service.domain.repository.UserRepository;
import com.palja.user_service.domain.vo.UserRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {

	private final ManagerRepository managerRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void createManager(CreateManagerCommand command) {
		validateDuplicateLoginId(command.loginId());
		validateDuplicateName(command.name());
		validateDuplicateEmail(command.email());

		User user = User.builder()
			.loginId(command.loginId())
			.password(passwordEncoder.encode(command.password()))
			.name(command.name())
			.role(UserRole.MANAGER)
			.build();

		Manager manager = Manager.builder()
			.user(user)
			.name(command.name())
			.email(command.email())
			.build();

		managerRepository.save(manager);
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
		if (managerRepository.existsByEmailAndDeletedAtIsNull(email)) {
			throw new BusinessException(CommonErrorCode.DOMAIN_ERROR);
		}
	}

}
