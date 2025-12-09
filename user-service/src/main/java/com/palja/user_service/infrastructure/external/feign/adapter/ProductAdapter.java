package com.palja.user_service.infrastructure.external.feign.adapter;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.common.response.ApiResponse;
import com.palja.user_service.application.service.ProductService;
import com.palja.user_service.infrastructure.external.feign.ProductClient;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductAdapter implements ProductService {

	private final ProductClient productClient;

	@Override
	public void deleteAllProducts(UUID companyUserId) {
		try {
			getDummy(companyUserId); // TODO: API 개발 완료 후 실제 호출로 변경
			// productClient.deleteAllProducts(companyUserId);
		} catch (FeignException e) {
			log.error("[Feign] status={} url=[{}] {} message={}",
				e.status(), e.request().httpMethod().name(), e.request().url(), e.contentUTF8());
			throw new BusinessException(CommonErrorCode.FEIGN_ERROR);
		}
	}

	private ApiResponse<Void> getDummy(UUID companyUserId) {
		return ApiResponse.success("상품이 삭제되었습니다.");
	}

}
