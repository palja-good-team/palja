package com.palja.user_service.application.service.impl;

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
import com.palja.user_service.application.command.CreateManagerCommand;
import com.palja.user_service.application.command.UpdateManagerCommand;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.dto.response.ReadManagerDetailRes;
import com.palja.user_service.application.dto.response.ReadManagerSummaryRes;
import com.palja.user_service.application.dto.response.UpdateManagerDetailRes;
import com.palja.user_service.application.exception.UserErrorCode;
import com.palja.user_service.application.service.ManagerService;
import com.palja.user_service.domain.entity.User;
import com.palja.user_service.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagerServiceImpl implements ManagerService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final CacheManager cacheManager;

	@Override
	@Transactional
	public CreateUserRes createManager(CreateManagerCommand command) {
		validateDuplicateLoginId(command.loginId());
		validateDuplicateEmail(command.email());

		User user = User.create(
			command.loginId(), passwordEncoder.encode(command.password()),
			command.name(), command.email(), command.address(), UserRole.MANAGER
		);

		AuditorContext.set(user.getLoginId(), user.getRole());
		userRepository.save(user);

		return CreateUserRes.from(user);
	}

	@Override
	public PageResponse<ReadManagerSummaryRes> getAllManagers(
		String currentUserLoginId, String loginId, String email, String name, Pageable pageable
	) {
		validateUserExistsByLoginId(currentUserLoginId);

		return PageResponse.from(
			userRepository.searchAllManagers(loginId, email, name, pageable)
				.map(ReadManagerSummaryRes::from)
		);
	}

	@Override
	@Cacheable(cacheNames = "user:manager", key = "'loginId:' + #loginId")
	public ReadManagerDetailRes getManagerByLoginId(String currentUserLoginId, String loginId) {
		validateUserExistsByLoginId(currentUserLoginId);

		return ReadManagerDetailRes.from(getManagerByLoginId(loginId));
	}

	@Override
	@Cacheable(cacheNames = "user:manager", key = "'userId:' + #userId")
	public ReadManagerDetailRes getManagerByUserId(String currentUserLoginId, Long userId) {
		validateUserExistsByLoginId(currentUserLoginId);

		return ReadManagerDetailRes.from(getManagerByUserId(userId));
	}

	@Override
	@Cacheable(cacheNames = "user:manager", key = "'loginId:' + #currentUserLoginId")
	public ReadManagerDetailRes getMe(String currentUserLoginId) {
		return ReadManagerDetailRes.from(getUserByLoginId(currentUserLoginId));
	}

	@Override
	@Transactional
	@Caching(evict = {
		@CacheEvict(cacheNames = "user:manager", key = "'loginId:' + #result.loginId"),
		@CacheEvict(cacheNames = "user:manager", key = "'userId:' + #result.userId")
	})
	public UpdateManagerDetailRes updateManagerByLoginId(String loginId, UpdateManagerCommand command) {
		User user = getManagerByLoginId(loginId);
		user.update(command.address());

		return UpdateManagerDetailRes.from(user);
	}

	@Override
	@Transactional
	@Caching(evict = {
		@CacheEvict(cacheNames = "user:manager", key = "'loginId:' + #result.loginId"),
		@CacheEvict(cacheNames = "user:manager", key = "'userId:' + #result.userId")
	})
	public UpdateManagerDetailRes updateMe(String currentUserLoginId, UpdateManagerCommand command) {
		User user = getUserByLoginId(currentUserLoginId);
		user.update(command.address());

		return UpdateManagerDetailRes.from(user);
	}

	@Override
	@Transactional
	public void deleteManagerByLoginId(String loginId) {
		User user = getManagerByLoginId(loginId);
		user.softDelete();

		removeCache(user);
	}

	private User getUserByLoginId(String loginId) {
		return userRepository.findByLoginIdAndDeletedAtIsNull(loginId).orElseThrow(
			() -> new BusinessException(UserErrorCode.USER_NOT_FOUND)
		);
	}

	private User getManagerByLoginId(String loginId) {
		return userRepository.findByLoginIdAndRoleAndDeletedAtIsNull(loginId, UserRole.MANAGER).orElseThrow(
			() -> new BusinessException(UserErrorCode.USER_NOT_FOUND)
		);
	}

	private User getManagerByUserId(Long userId) {
		return userRepository.findByIdAndRoleAndDeletedAtIsNull(userId, UserRole.MANAGER).orElseThrow(
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

	private void removeCache(User user) {
		Cache cache = cacheManager.getCache("user:manager");
		if (cache != null) {
			cache.evict("loginId:" + user.getLoginId());
			cache.evict("userId:" + user.getId());
		}
	}

}
