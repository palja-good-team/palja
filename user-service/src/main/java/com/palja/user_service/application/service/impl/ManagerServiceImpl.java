package com.palja.user_service.application.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.palja.common.auditor.AuditorContext;
import com.palja.common.exception.BusinessException;
import com.palja.common.vo.UserRole;
import com.palja.user_service.application.command.CreateManagerCommand;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.exception.UserErrorCode;
import com.palja.user_service.application.service.ManagerService;
import com.palja.user_service.domain.entity.User;
import com.palja.user_service.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public CreateUserRes createManager(String currentUserLoginId, CreateManagerCommand command) {
		getUserByLoginId(currentUserLoginId);
		validateDuplicateLoginId(command.loginId());
		validateDuplicateEmail(command.email());

		User user = User.builder()
			.loginId(command.loginId())
			.password(passwordEncoder.encode(command.password()))
			.name(command.name())
			.email(command.email())
			.address(command.address())
			.role(UserRole.MANAGER)
			.build();

		AuditorContext.set(user.getLoginId(), user.getRole());
		userRepository.save(user);

		return CreateUserRes.from(user);
	}

	private User getUserByLoginId(String loginId) {
		return userRepository.findByLoginIdAndDeletedAtIsNull(loginId).orElseThrow(
			() -> new BusinessException(UserErrorCode.USER_NOT_FOUND)
		);
	}

	private void validateDuplicateLoginId(String loginId) {
		if (userRepository.existsByLoginIdAndDeletedAtIsNull(loginId)) {
			throw new BusinessException(UserErrorCode.DUPLICATED_LOGIN_ID);
		}
	}

	private void validateDuplicateEmail(String email) {
		if (userRepository.existsByEmailAndDeletedAtIsNull(email)) {
			throw new BusinessException(UserErrorCode.DUPLICATED_EMAIL);
		}
	}

}
