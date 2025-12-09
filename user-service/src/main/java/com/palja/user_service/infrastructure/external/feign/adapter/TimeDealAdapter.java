package com.palja.user_service.infrastructure.external.feign.adapter;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.user_service.application.service.TimeDealService;
import com.palja.user_service.infrastructure.external.feign.TimeDealClient;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeDealAdapter implements TimeDealService {

	private final TimeDealClient timeDealClient;

	@Override
	public void deleteAllTimeDeals(UUID companyUserId) {
		try {
			timeDealClient.deleteAllTimeDeals(companyUserId);
		} catch (FeignException e) {
			log.error("[Feign] status={} url=[{}] {} message={}",
				e.status(), e.request().httpMethod().name(), e.request().url(), e.contentUTF8());
			throw new BusinessException(CommonErrorCode.FEIGN_ERROR);
		}
	}

}
