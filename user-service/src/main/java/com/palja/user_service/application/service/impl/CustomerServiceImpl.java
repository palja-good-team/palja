package com.palja.user_service.application.service.impl;

import static com.palja.user_service.application.util.RedisKeyConstants.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.palja.common.auditor.AuditorContext;
import com.palja.common.exception.BusinessException;
import com.palja.common.response.PageResponse;
import com.palja.common.vo.UserRole;
import com.palja.user_service.application.command.CreateCustomerCommand;
import com.palja.user_service.application.command.UpdateCustomerCommand;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.dto.response.ReadCustomerDetailRes;
import com.palja.user_service.application.dto.response.ReadCustomerSummaryRes;
import com.palja.user_service.application.dto.response.UpdateCustomerDetailRes;
import com.palja.user_service.application.event.dto.impl.DeleteCustomerEventReq;
import com.palja.user_service.application.exception.AuthErrorCode;
import com.palja.user_service.application.exception.UserErrorCode;
import com.palja.user_service.application.service.CustomerService;
import com.palja.user_service.application.util.JwtUtil;
import com.palja.user_service.domain.entity.User;
import com.palja.user_service.domain.repository.TokenRepository;
import com.palja.user_service.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;

	private final ApplicationEventPublisher applicationEventPublisher;

	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Override
	@Transactional
	public CreateUserRes createCustomer(CreateCustomerCommand command) {
		validateDuplicateLoginId(command.loginId());
		validateDuplicateEmail(command.email());

		User user = User.create(
			command.loginId(), passwordEncoder.encode(command.password()),
			command.name(), command.email(), command.address(), UserRole.CUSTOMER
		);

		AuditorContext.set(user.getLoginId(), user.getRole());
		userRepository.save(user);

		return CreateUserRes.from(user);
	}

	@Override
	public PageResponse<ReadCustomerSummaryRes> getAllCustomers(
		String currentUserLoginId, String loginId, String email, String name, Pageable pageable
	) {
		validateUserExistsByLoginId(currentUserLoginId);

		return PageResponse.from(
			userRepository.searchAllCustomers(loginId, email, name, pageable)
				.map(ReadCustomerSummaryRes::from)
		);
	}

	@Override
	public ReadCustomerDetailRes getCustomerByLoginId(String currentUserLoginId, String loginId) {
		validateUserExistsByLoginId(currentUserLoginId);

		return ReadCustomerDetailRes.from(getCustomerByLoginId(loginId));
	}

	@Override
	public ReadCustomerDetailRes getCustomerByUserId(String currentUserLoginId, Long userId) {
		validateUserExistsByLoginId(currentUserLoginId);

		return ReadCustomerDetailRes.from(getCustomerByUserId(userId));
	}

	@Override
	public ReadCustomerDetailRes getMe(String currentUserLoginId) {
		return ReadCustomerDetailRes.from(getCustomerByLoginId(currentUserLoginId));
	}

	@Override
	@Transactional
	public UpdateCustomerDetailRes updateCustomerByLoginId(
		String currentUserLoginId, String loginId, UpdateCustomerCommand command
	) {
		validateUserExistsByLoginId(currentUserLoginId);

		User user = getCustomerByLoginId(loginId);
		user.update(command.address());

		return UpdateCustomerDetailRes.from(user);
	}

	@Override
	@Transactional
	public UpdateCustomerDetailRes updateMe(String currentUserLoginId, UpdateCustomerCommand command) {
		User user = getCustomerByLoginId(currentUserLoginId);
		user.update(command.address());

		return UpdateCustomerDetailRes.from(user);
	}

	@Override
	@Transactional
	public void deleteCustomerByLoginId(String currentUserLoginId, String loginId) {
		validateUserExistsByLoginId(currentUserLoginId);

		User user = getCustomerByLoginId(loginId);
		user.softDelete();

		applicationEventPublisher.publishEvent(DeleteCustomerEventReq.from(user.getId()));
	}

	@Override
	@Transactional
	public void deleteMe(String accessToken, String currentUserLoginId) {
		validateTokenIsNotNull(accessToken);

		User user = getCustomerByLoginId(currentUserLoginId);
		user.softDelete();

		applicationEventPublisher.publishEvent(DeleteCustomerEventReq.from(user.getId()));

		String substringAccessToken = jwtUtil.substringToken(accessToken);
		String hashKey = jwtUtil.hashingTokenToSHA256(substringAccessToken);

		tokenRepository.save(
			ACCESS_TOKEN_BLACKLIST_PREFIX + currentUserLoginId + ":" + hashKey, substringAccessToken, jwtUtil.getAccessKeyExpirationTime()
		);
		tokenRepository.remove(REFRESH_TOKEN_WHITELIST_PREFIX + currentUserLoginId);
	}

	private User getCustomerByLoginId(String loginId) {
		return userRepository.findByLoginIdAndRoleAndDeletedAtIsNull(loginId, UserRole.CUSTOMER).orElseThrow(
			() -> new BusinessException(UserErrorCode.USER_NOT_FOUND)
		);
	}

	private User getCustomerByUserId(Long userId) {
		return userRepository.findByIdAndRoleAndDeletedAtIsNull(userId, UserRole.CUSTOMER).orElseThrow(
			() -> new BusinessException(UserErrorCode.USER_NOT_FOUND)
		);
	}

	private void validateUserExistsByLoginId(String loginId) {
		if (!userRepository.existsByLoginIdAndDeletedAtIsNull(loginId)) {
			throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
		}
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

	private void validateTokenIsNotNull(String token) {
		if (token == null) {
			throw new BusinessException(AuthErrorCode.NOT_FOUND_TOKEN);
		}
	}

}
