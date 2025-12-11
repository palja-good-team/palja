package com.palja.user_service.infrastructure.external.feign.adapter;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.common.response.ApiResponse;
import com.palja.user_service.application.port.TimeDealClient;
import com.palja.user_service.infrastructure.external.feign.TimeDealFeignClient;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeDealAdapter implements TimeDealClient {

	private final TimeDealFeignClient timeDealFeignClient;

	@Override
	public void deleteAllTimeDeals(UUID companyUserId) {
		try {
			getDummy(companyUserId); // TODO: API 개발 완료 후 실제 호출로 변경
			// timeDealFeignClient.deleteAllTimeDeals(companyUserId);
		} catch (FeignException e) {
			log.error("[Feign] status={} url=[{}] {} message={}",
				e.status(), e.request().httpMethod().name(), e.request().url(), e.contentUTF8());
			throw new BusinessException(CommonErrorCode.FEIGN_ERROR);
		}
	}

	private ApiResponse<Void> getDummy(UUID companyUserId) {
		return ApiResponse.success("타임딜이 삭제되었습니다.");
	}

}
