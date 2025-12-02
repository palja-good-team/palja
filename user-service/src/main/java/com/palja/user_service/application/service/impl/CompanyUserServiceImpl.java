package com.palja.user_service.application.service.impl;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.user_service.application.command.CreateCompanyUserCommand;
import com.palja.user_service.application.command.UpdateCompanyUserStatusCommand;
import com.palja.user_service.application.service.CompanyUserService;
import com.palja.user_service.domain.entity.CompanyUser;
import com.palja.user_service.domain.entity.User;
import com.palja.user_service.domain.repository.CompanyUserRepository;
import com.palja.user_service.domain.repository.UserRepository;
import com.palja.user_service.domain.vo.UserRole;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyUserServiceImpl implements CompanyUserService {

	private final CompanyUserRepository companyUserRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional
	public void createCompanyUser(CreateCompanyUserCommand command) {
		validateDuplicateLoginId(command.loginId());
		validateDuplicateName(command.name());
		validateDuplicateEmail(command.email());

		User user = User.builder()
			.loginId(command.loginId())
			.password(passwordEncoder.encode(command.password()))
			.name(command.name())
			.role(UserRole.COMPANY_USER)
			.build();

		CompanyUser companyUser = CompanyUser.builder()
			.user(user)
			.name(command.name())
			.companyNumber(command.companyNumber())
			.email(command.email())
			.address(command.address())
			.build();

		companyUserRepository.save(companyUser);
	}

	@Override
	@Transactional
	public void updateCompanyUserStatus(UUID companyUserId, UpdateCompanyUserStatusCommand command) {
		CompanyUser companyUser = getCompanyUserById(companyUserId);
		companyUser.updateStatus(command.status());
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
		if (companyUserRepository.existsByEmailAndDeletedAtIsNull(email)) {
			throw new BusinessException(CommonErrorCode.DOMAIN_ERROR);
		}
	}

	private CompanyUser getCompanyUserById(UUID companyUserId) {
		return companyUserRepository.findByIdAndDeletedAtIsNull(companyUserId).orElseThrow(
			() -> new BusinessException(CommonErrorCode.DOMAIN_ERROR)
		);
	}

}
