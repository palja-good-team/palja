package com.palja.timedeal_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.timedeal_service.application.command.CreateTimeDealCommand;
import com.palja.timedeal_service.application.dto.TimeDealDetailRes;
import com.palja.timedeal_service.application.dto.external.ProductInfo;
import com.palja.timedeal_service.application.port.ProductClient;
import com.palja.timedeal_service.application.service.TimeDealService;
import com.palja.timedeal_service.application.validator.AuthorityValidator;
import com.palja.timedeal_service.common.TimeDealErrorCode;
import com.palja.timedeal_service.domain.entity.TimeDeal;
import com.palja.timedeal_service.domain.repository.TimeDealRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeDealServiceImpl implements TimeDealService {

    private final TimeDealRepository timeDealRepository;
    private final ProductClient productClient;
    private final AuthorityValidator authorityValidator;

    @Override
    @Transactional
    public TimeDealDetailRes createTimeDeal(CreateTimeDealCommand command) {
        log.info("타임딜 생성 시작");

        ProductInfo product = productClient.getProduct(command.productId());

        UUID companyUserId = authorityValidator.verifyCompanyUserId(
                command.loginId(),
                command.role(),
                product.companyUserId()
        );

        validateStock(command, product);

        TimeDeal timeDeal = TimeDeal.create(
                command.productId(),
                companyUserId,
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

    private void validateStock(CreateTimeDealCommand command, ProductInfo product) {
        if (product.stock() < command.totalQuantity()) {
            throw new BusinessException(TimeDealErrorCode.INVALID_STOCK_QUANTITY);
        }
    }
}
