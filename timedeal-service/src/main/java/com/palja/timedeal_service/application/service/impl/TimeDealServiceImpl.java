package com.palja.timedeal_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.common.vo.UserRole;
import com.palja.timedeal_service.application.command.CreateTimeDealCommand;
import com.palja.timedeal_service.application.command.UpdateTimeDealCommand;
import com.palja.timedeal_service.application.dto.TimeDealDetailRes;
import com.palja.timedeal_service.application.dto.external.ProductInfo;
import com.palja.timedeal_service.application.port.ProductClient;
import com.palja.timedeal_service.application.service.TimeDealService;
import com.palja.timedeal_service.application.validator.TimeDealValidator;
import com.palja.timedeal_service.common.TimeDealEditableField;
import com.palja.timedeal_service.domain.entity.TimeDeal;
import com.palja.timedeal_service.domain.repository.TimeDealRepository;
import com.palja.timedeal_service.domain.vo.Amount;
import com.palja.timedeal_service.domain.vo.Period;
import com.palja.timedeal_service.domain.vo.Quantity;
import com.palja.timedeal_service.domain.vo.TimeDealStatus;
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
            timeDealValidator.validateCompanyUserId(command.loginId(), product.companyUserId());
        }

        timeDealValidator.validateStock(command.totalQuantity(), product.stock());

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

    @Override
    @Transactional
    public TimeDealDetailRes updateTimeDeal(UpdateTimeDealCommand command) {
        log.info("타임딜 수정 시작");

        TimeDeal timeDeal = getActiveTimeDeal(command.timeDealId());

        if (command.role().equals(UserRole.COMPANY_USER)) {
            timeDealValidator.validateCompanyUserId(command.loginId(), timeDeal.getCompanyUserId());
        }

        updateTimeDealFields(timeDeal, command);

        log.info("타임딜 수정 완료");
        return TimeDealDetailRes.from(timeDeal);
    }

    private void updateTimeDealFields(TimeDeal timeDeal, UpdateTimeDealCommand command) {
        TimeDealStatus timeDealStatus = timeDeal.getTimeDealStatus();

        if (command.title() != null) {
            timeDealValidator.validateEditable(timeDealStatus, TimeDealEditableField.TITLE);
            timeDeal.changeTitle(command.title());
        }

        if (command.description() != null) {
            timeDealValidator.validateEditable(timeDealStatus, TimeDealEditableField.DESCRIPTION);
            timeDeal.changeDescription(command.description());
        }

        if (command.startAt() != null) {
            timeDealValidator.validateEditable(timeDealStatus, TimeDealEditableField.START_AT);
            timeDeal.changeStartAt(command.startAt());
        }

        if (command.endAt() != null) {
            timeDealValidator.validateEditable(timeDealStatus, TimeDealEditableField.END_AT);
            timeDeal.changeEndAt(command.endAt());
        }

        if (command.timeDealPrice() != null) {
            timeDealValidator.validateEditable(timeDealStatus, TimeDealEditableField.TITLE);
            timeDeal.changeTimeDealPrice(command.timeDealPrice());
        }

        if (command.totalQuantity() != null) {
            timeDealValidator.validateEditable(timeDealStatus, TimeDealEditableField.TOTAL_QUANTITY);
            timeDeal.changeTotalQuantity(command.totalQuantity());
        }
    }

    private TimeDeal getActiveTimeDeal(UUID timeDealId) {
        return timeDealRepository.findDetailByTimeDealId(timeDealId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));
    }
}
