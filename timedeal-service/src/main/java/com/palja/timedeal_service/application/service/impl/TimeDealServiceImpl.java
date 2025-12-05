package com.palja.timedeal_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.common.vo.UserRole;
import com.palja.timedeal_service.application.command.CreateTimeDealCommand;
import com.palja.timedeal_service.application.dto.TimeDealDetailRes;
import com.palja.timedeal_service.application.dto.external.ProductInfo;
import com.palja.timedeal_service.application.port.ProductClient;
import com.palja.timedeal_service.application.service.TimeDealService;
import com.palja.timedeal_service.application.validator.TimeDealValidator;
import com.palja.timedeal_service.domain.entity.TimeDeal;
import com.palja.timedeal_service.domain.repository.TimeDealRepository;
import com.palja.timedeal_service.domain.vo.Amount;
import com.palja.timedeal_service.domain.vo.Period;
import com.palja.timedeal_service.domain.vo.Quantity;
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
    private final TimeDealValidator timeDealValidator;

    @Override
    @Transactional
    public TimeDealDetailRes createTimeDeal(CreateTimeDealCommand command) {
        log.info("타임딜 생성 시작");

        ProductInfo product = productClient.getProduct(command.productId());

        if (command.role().equals(UserRole.COMPANY_USER)) {
            timeDealValidator.verifyCompanyUserId(command.loginId(), product.companyUserId());
        }

        timeDealValidator.verifyStock(command.totalQuantity(), product.stock());

        Period period = Period.of(command.startAt(), command.endAt());
        Amount amount = Amount.of(product.price(), command.timeDealPrice());
        Quantity quantity = Quantity.of(command.totalQuantity());

        TimeDeal timeDeal = TimeDeal.create(
                command.productId(),
                product.companyUserId(),
                command.title(),
                command.description(),
                period,
                amount,
                quantity
        );

        TimeDeal savedTimeDeal = timeDealRepository.save(timeDeal);

        log.info("타임딜 생성 완료: timeDealId = {}", savedTimeDeal.getTimeDealId());
        return TimeDealDetailRes.from(savedTimeDeal);
    }

    @Override
    public TimeDealDetailRes getTimeDeal(UUID timeDealId) {
        log.info("타임딜 상세조회 시작");

        TimeDeal timeDeal = getActiveTimeDeal(timeDealId);

        log.info("타임딜 상세조회 완료");
        return TimeDealDetailRes.from(timeDeal);
    }

    private TimeDeal getActiveTimeDeal(UUID timeDealId) {
        return timeDealRepository.findDetailByTimeDealId(timeDealId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));
    }
}
