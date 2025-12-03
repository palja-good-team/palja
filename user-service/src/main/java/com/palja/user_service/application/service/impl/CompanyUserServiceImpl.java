package com.palja.user_service.application.service.impl;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.palja.common.auditor.AuditorContext;
import com.palja.common.exception.BusinessException;
import com.palja.common.vo.UserRole;
import com.palja.user_service.application.command.CreateCompanyUserCommand;
import com.palja.user_service.application.command.UpdateCompanyUserStatusCommand;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.exception.UserErrorCode;
import com.palja.user_service.application.service.CompanyUserService;
import com.palja.user_service.domain.entity.CompanyUser;
import com.palja.user_service.domain.entity.User;
import com.palja.user_service.domain.repository.CompanyUserRepository;
import com.palja.user_service.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyUserServiceImpl implements CompanyUserService {

	private final CompanyUserRepository companyUserRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public CreateUserRes createCompanyUser(CreateCompanyUserCommand command) {
		validateDuplicateLoginId(command.loginId());
		validateDuplicateEmail(command.email());

		User user = User.builder()
			.loginId(command.loginId())
			.password(passwordEncoder.encode(command.password()))
			.name(command.name())
			.email(command.email())
			.address(command.address())
			.role(UserRole.COMPANY_USER)
			.build();

		CompanyUser companyUser = CompanyUser.builder()
			.user(user)
			.companyName(command.companyName())
			.companyNumber(command.companyNumber())
			.build();

		AuditorContext.set(user.getLoginId(), user.getRole());
		companyUserRepository.save(companyUser);

		return CreateUserRes.from(user);
	}

	@Override
	@Transactional
	public void updateCompanyUserStatus(UUID companyUserId, UpdateCompanyUserStatusCommand command) {
		CompanyUser companyUser = getCompanyUserById(companyUserId);
		companyUser.updateStatus(command.status());
	}

	private CompanyUser getCompanyUserById(UUID companyUserId) {
		return companyUserRepository.findByIdAndDeletedAtIsNull(companyUserId).orElseThrow(
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
