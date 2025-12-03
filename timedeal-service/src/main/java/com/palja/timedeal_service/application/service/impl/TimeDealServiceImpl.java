package com.palja.timedeal_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.timedeal_service.application.command.CreateTimeDealCommand;
import com.palja.timedeal_service.application.dto.TimeDealDetailRes;
import com.palja.timedeal_service.application.dto.external.ProductInfo;
import com.palja.timedeal_service.application.port.ProductClient;
import com.palja.timedeal_service.application.service.TimeDealService;
import com.palja.timedeal_service.common.TimeDealErrorCode;
import com.palja.timedeal_service.domain.entity.TimeDeal;
import com.palja.timedeal_service.domain.repository.TimeDealRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeDealServiceImpl implements TimeDealService {

    private final TimeDealRepository timeDealRepository;
    private final ProductClient productClient;

    @Override
    @Transactional
    public TimeDealDetailRes createTimeDeal(CreateTimeDealCommand command) {
        log.info("타임딜 생성 시작");

        ProductInfo product = productClient.getProduct(command.productId());

        validateAuthority(command, product);
        validateStock(command, product);

        TimeDeal timeDeal = TimeDeal.create(
                command.productId(),
                command.companyUserId(),
                command.title(),
                command.description(),
                command.startAt(),
                command.endAt(),
                product.price(),
                command.timeDealPrice(),
                command.totalQuantity()
        );

        TimeDeal savedTimeDeal = timeDealRepository.save(timeDeal);

        log.info("타임딜 생성 완료: timeDealId = {}", savedTimeDeal.getTimeDealId());
        return TimeDealDetailRes.from(savedTimeDeal);
    }

    private void validateAuthority(CreateTimeDealCommand command, ProductInfo product) {
        if (command.role().equals("CUSTOMER")) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }

        if (command.role().equals("COMPANY_USER")) {
            if (!product.companyUserId().equals(command.companyUserId())) {
                throw new BusinessException(CommonErrorCode.FORBIDDEN);
            }
        }
    }

    private void validateStock(CreateTimeDealCommand command, ProductInfo product) {
        if (product.stock() < command.totalQuantity()) {
            throw new BusinessException(TimeDealErrorCode.INVALID_STOCK_QUANTITY);
        }
    }
}
