package com.palja.user_service.application.service;

import org.springframework.data.domain.Pageable;

import com.palja.common.response.PageResponse;
import com.palja.user_service.application.command.CreateManagerCommand;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.dto.response.ReadManagerDetailRes;
import com.palja.user_service.application.dto.response.ReadManagerSummaryRes;

public interface ManagerService {

	CreateUserRes createManager(String currentUserLoginId, CreateManagerCommand command);

	PageResponse<ReadManagerSummaryRes> getAllManagers(
		String currentUserLoginId, String loginId, String email, String name, Pageable pageable
	);

	ReadManagerDetailRes getManagerByLoginId(String currentUserLoginId, String loginId);

	ReadManagerDetailRes getManagerByUserId(String currentUserLoginId, Long userId);

	ReadManagerDetailRes getMe(String currentUserLoginId);

}
