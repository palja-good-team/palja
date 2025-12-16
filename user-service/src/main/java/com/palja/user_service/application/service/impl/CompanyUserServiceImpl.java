package com.palja.user_service.application.service.impl;

import static com.palja.user_service.application.util.RedisKeyConstants.*;

import java.util.UUID;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.palja.common.auditor.AuditorContext;
import com.palja.common.exception.BusinessException;
import com.palja.common.response.PageResponse;
import com.palja.common.vo.UserRole;
import com.palja.user_service.application.command.CreateCompanyUserCommand;
import com.palja.user_service.application.command.UpdateCompanyUserCommand;
import com.palja.user_service.application.command.UpdateCompanyUserStatusCommand;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.dto.response.ReadCompanyUserDetailRes;
import com.palja.user_service.application.dto.response.ReadCompanyUserSummaryRes;
import com.palja.user_service.application.dto.response.UpdateCompanyUserDetailRes;
import com.palja.user_service.application.exception.AuthErrorCode;
import com.palja.user_service.application.exception.UserErrorCode;
import com.palja.user_service.application.port.ProductClient;
import com.palja.user_service.application.port.TimeDealClient;
import com.palja.user_service.application.service.CompanyUserService;
import com.palja.user_service.application.util.JwtUtil;
import com.palja.user_service.domain.entity.CompanyUser;
import com.palja.user_service.domain.entity.User;
import com.palja.user_service.domain.repository.CompanyUserRepository;
import com.palja.user_service.domain.repository.TokenRepository;
import com.palja.user_service.domain.repository.UserRepository;
import com.palja.user_service.domain.vo.UserStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyUserServiceImpl implements CompanyUserService {

	private final CompanyUserRepository companyUserRepository;
	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;

	private final TimeDealClient timeDealClient;
	private final ProductClient productClient;

	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final CacheManager cacheManager;

	@Override
	@Transactional
	public CreateUserRes createCompanyUser(CreateCompanyUserCommand command) {
		validateDuplicateLoginId(command.loginId());
		validateDuplicateEmail(command.email());

		User user = User.create(
			command.loginId(), passwordEncoder.encode(command.password()),
			command.name(), command.email(), command.address(), UserRole.COMPANY_USER
		);

		CompanyUser companyUser = CompanyUser.create(user, command.companyName(), command.companyNumber());

		AuditorContext.set(user.getLoginId(), user.getRole());
		companyUserRepository.save(companyUser);

		return CreateUserRes.from(user);
	}

	@Override
	public PageResponse<ReadCompanyUserSummaryRes> getAllCompanyUsers(
		String currentUserLoginId, String loginId, String email, String name, String status, Pageable pageable
	) {
		validateUserExistsByLoginId(currentUserLoginId);

		return PageResponse.from(
			userRepository.searchAllCompanyUsers(loginId, email, name, validateAndGetUserStatus(status), pageable)
				.map(ReadCompanyUserSummaryRes::from)
		);
	}

	@Override
	@Cacheable(cacheNames = COMPANY_USER_CACHE_PREFIX, key = "'loginId:' + #loginId")
	public ReadCompanyUserDetailRes getCompanyUserByLoginId(String currentUserLoginId, String loginId) {
		validateUserExistsByLoginId(currentUserLoginId);

		return ReadCompanyUserDetailRes.from(getCompanyUserByLoginId(loginId));
	}

	@Override
	@Cacheable(cacheNames = COMPANY_USER_CACHE_PREFIX, key = "'companyUserId:' + #companyUserId")
	public ReadCompanyUserDetailRes getCompanyUserByCompanyUserId(String currentUserLoginId, UUID companyUserId) {
		validateUserExistsByLoginId(currentUserLoginId);

		return ReadCompanyUserDetailRes.from(getCompanyUserByCompanyUserId(companyUserId));
	}

	@Override
	@Cacheable(cacheNames = COMPANY_USER_CACHE_PREFIX, key = "'loginId:' + #currentUserLoginId")
	public ReadCompanyUserDetailRes getMe(String currentUserLoginId) {
		return ReadCompanyUserDetailRes.from(getCompanyUserByLoginId(currentUserLoginId));
	}

	@Override
	@Transactional
	@Caching(evict = {
		@CacheEvict(cacheNames = COMPANY_USER_CACHE_PREFIX, key = "'loginId:' + #result.loginId"),
		@CacheEvict(cacheNames = COMPANY_USER_CACHE_PREFIX, key = "'companyUserId:' + #result.companyUserId")
	})
	public UpdateCompanyUserDetailRes updateCompanyUserByLoginId(
		String currentUserLoginId, String loginId, UpdateCompanyUserCommand command
	) {
		validateUserExistsByLoginId(currentUserLoginId);

		CompanyUser companyUser = getCompanyUserByLoginId(loginId);
		companyUser.update(command.companyName(), command.address());

		return UpdateCompanyUserDetailRes.from(companyUser);
	}

	@Override
	@Transactional
	@Caching(evict = {
		@CacheEvict(cacheNames = COMPANY_USER_CACHE_PREFIX, key = "'loginId:' + #result.loginId"),
		@CacheEvict(cacheNames = COMPANY_USER_CACHE_PREFIX, key = "'companyUserId:' + #result.companyUserId")
	})
	public UpdateCompanyUserDetailRes updateMe(String currentUserLoginId, UpdateCompanyUserCommand command) {
		CompanyUser companyUser = getCompanyUserByLoginId(currentUserLoginId);
		companyUser.update(command.companyName(), command.address());

		return UpdateCompanyUserDetailRes.from(companyUser);
	}

	@Override
	@Transactional
	public void updateCompanyUserStatus(String currentUserLoginId, String loginId, UpdateCompanyUserStatusCommand command) {
		validateUserExistsByLoginId(currentUserLoginId);

		CompanyUser companyUser = getCompanyUserByLoginId(loginId);
		companyUser.updateStatus(command.status());

		removeCache(companyUser);
	}

	@Override
	@Transactional
	public void deleteCompanyUserByLoginId(String currentUserLoginId, String loginId) {
		validateUserExistsByLoginId(currentUserLoginId);

		CompanyUser companyUser = getCompanyUserByLoginId(loginId);
		companyUser.softDelete();
		timeDealClient.deleteAllTimeDeals(companyUser.getId());
		productClient.deleteAllProducts(companyUser.getId());

		removeCache(companyUser);
	}

	@Override
	@Transactional
	public void deleteMe(String accessToken, String currentUserLoginId) {
		validateTokenIsNotNull(accessToken);

		CompanyUser companyUser = getCompanyUserByLoginId(currentUserLoginId);
		companyUser.softDelete();
		timeDealClient.deleteAllTimeDeals(companyUser.getId());
		productClient.deleteAllProducts(companyUser.getId());

		String substringAccessToken = jwtUtil.substringToken(accessToken);
		String hashKey = jwtUtil.hashingTokenToSHA256(substringAccessToken);

		tokenRepository.save(
			ACCESS_TOKEN_BLACKLIST_PREFIX + currentUserLoginId + ":" + hashKey, substringAccessToken, jwtUtil.getAccessKeyExpirationTime()
		);
		tokenRepository.remove(REFRESH_TOKEN_WHITELIST_PREFIX + currentUserLoginId);

		removeCache(companyUser);
	}

	@Override
	@Transactional
	public void rejectCompanyUser(String currentUserLoginId, String loginId) {
		validateUserExistsByLoginId(currentUserLoginId);

		CompanyUser companyUser = getCompanyUserByLoginId(loginId);
		validateStatusIsPending(companyUser);
		companyUser.softDelete();

		removeCache(companyUser);
	}

	private CompanyUser getCompanyUserByLoginId(String loginId) {
		return companyUserRepository.findByLoginIdAndDeletedAtIsNull(loginId).orElseThrow(
			() -> new BusinessException(UserErrorCode.USER_NOT_FOUND)
		);
	}

	private CompanyUser getCompanyUserByCompanyUserId(UUID companyUserId) {
		return companyUserRepository.findByIdAndDeletedAtIsNull(companyUserId).orElseThrow(
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

	private void validateStatusIsPending(CompanyUser companyUser) {
		if (companyUser.getUser().getStatus().equals(UserStatus.ACTIVE)) {
			throw new BusinessException(UserErrorCode.COMPANY_USER_STATUS_ALREADY_ACTIVE);
		}
	}

	private UserStatus validateAndGetUserStatus(String status) {
		if (status == null) return null;

		try {
			return UserStatus.valueOf(status.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new BusinessException(UserErrorCode.USER_STATUS_NOT_FOUND);
		}
	}

	private void validateTokenIsNotNull(String token) {
		if (token == null) {
			throw new BusinessException(AuthErrorCode.NOT_FOUND_TOKEN);
		}
	}

	private void removeCache(CompanyUser companyUser) {
		Cache cache = cacheManager.getCache(COMPANY_USER_CACHE_PREFIX);
		if (cache != null) {
			cache.evict("loginId:" + companyUser.getUser().getLoginId());
			cache.evict("companyUserId:" + companyUser.getId());
		}
	}

}
