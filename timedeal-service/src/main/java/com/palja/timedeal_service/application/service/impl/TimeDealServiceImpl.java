package com.palja.timedeal_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.common.response.PageResponse;
import com.palja.common.vo.UserRole;
import com.palja.timedeal_service.application.command.*;
import com.palja.timedeal_service.application.dto.TimeDealCreateRes;
import com.palja.timedeal_service.application.dto.TimeDealDetailRes;
import com.palja.timedeal_service.application.dto.TimeDealStatusChangeRes;
import com.palja.timedeal_service.application.dto.TimeDealUpdateRes;
import com.palja.timedeal_service.application.dto.external.ProductInfo;
import com.palja.timedeal_service.application.port.ProductClient;
import com.palja.timedeal_service.application.service.TimeDealService;
import com.palja.timedeal_service.application.validator.TimeDealValidator;
import com.palja.timedeal_service.common.TimeDealEditableField;
import com.palja.timedeal_service.domain.entity.TimeDeal;
import com.palja.timedeal_service.domain.entity.TimeDealStatusHistory;
import com.palja.timedeal_service.application.event.internal.TimeDealStockDecreaseEventReq;
import com.palja.timedeal_service.application.event.internal.TimeDealStockIncreaseEventReq;
import com.palja.timedeal_service.domain.repository.TimeDealRepository;
import com.palja.timedeal_service.domain.vo.Amount;
import com.palja.timedeal_service.domain.vo.Period;
import com.palja.timedeal_service.domain.vo.Quantity;
import com.palja.timedeal_service.domain.vo.TimeDealStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeDealServiceImpl implements TimeDealService {

    private final TimeDealRepository timeDealRepository;
    private final ProductClient productClient;
    private final TimeDealValidator timeDealValidator;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public TimeDealCreateRes createTimeDeal(CreateTimeDealCommand command) {
        log.info("타임딜 생성 시작");

        ProductInfo product = productClient.getProduct(command.productId());

        validateCompanyUser(command.role(), product.companyUserId());

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

        eventPublisher.publishEvent(new TimeDealStockIncreaseEventReq(
                savedTimeDeal.getTimeDealId(),
                savedTimeDeal.getProductId(),
                savedTimeDeal.getRemainingQuantity()
        ));

        log.info("타임딜 생성 완료: timeDealId = {}", savedTimeDeal.getTimeDealId());
        return TimeDealCreateRes.from(savedTimeDeal);
    }

    @Override
    public TimeDealDetailRes getTimeDeal(UUID timeDealId) {
        log.info("타임딜 상세조회 시작");

        TimeDeal timeDeal = getActiveTimeDeal(timeDealId);

        log.info("타임딜 상세조회 완료");
        return TimeDealDetailRes.from(timeDeal);
    }

    @Override
    public PageResponse<TimeDealDetailRes> getTimeDeals(Pageable pageable) {
        Page<TimeDeal> timeDeals = timeDealRepository.searchTimeDeals(pageable);

        Page<TimeDealDetailRes> timeDealDto = timeDeals.map(TimeDealDetailRes::from);

        return PageResponse.from(timeDealDto);
    }

    @Override
    @Transactional
    public TimeDealUpdateRes updateTimeDeal(UpdateTimeDealCommand command) {
        log.info("타임딜 수정 시작");

        TimeDeal timeDeal = getActiveTimeDeal(command.timeDealId());
        long oldTotal = timeDeal.getTotalQuantity();

        validateCompanyUser(command.role(), timeDeal.getCompanyUserId());

        updateTimeDealFields(timeDeal, command);

        long newTotal = timeDeal.getTotalQuantity();
        long delta = newTotal - oldTotal;

        if (delta > 0) {
            eventPublisher.publishEvent(new TimeDealStockIncreaseEventReq(
                    timeDeal.getTimeDealId(),
                    timeDeal.getProductId(),
                    delta
            ));
        } else if (delta < 0) {
            eventPublisher.publishEvent(new TimeDealStockDecreaseEventReq(
                    timeDeal.getTimeDealId(),
                    timeDeal.getProductId(),
                    -delta
            ));
        }

        log.info("타임딜 수정 완료");
        return TimeDealUpdateRes.from(timeDeal);
    }

    @Override
    @Transactional
    public TimeDealStatusChangeRes changeTimeDealStatus(ChangeTimeDealStatusCommand command) {
        log.info("타임딜 상태 수정 시작");

        TimeDeal timeDeal = getActiveTimeDeal(command.timeDealId());

        validateCompanyUser(command.role(), timeDeal.getCompanyUserId());

        TimeDealStatus newStatus = TimeDealStatus.from(command.newStatus());

        TimeDealStatusHistory timeDealStatusHistory = timeDeal.changeStatusBy(newStatus, command.reason());

        if (newStatus == TimeDealStatus.CLOSED) {
            eventPublisher.publishEvent(new TimeDealStockDecreaseEventReq(
                    timeDeal.getTimeDealId(),
                    timeDeal.getProductId(),
                    timeDeal.getRemainingQuantity()
            ));
        }

        log.info("타임딜 상태 수정 완료");
        return TimeDealStatusChangeRes.from(timeDeal, timeDealStatusHistory);
    }

    @Override
    @Transactional
    public void deleteTimeDeal(DeleteTimeDealCommand command) {
        log.info("타임딜 삭제 시작");

        TimeDeal timeDeal = getActiveTimeDeal(command.timeDealId());

        validateCompanyUser(command.role(), timeDeal.getCompanyUserId());

        timeDeal.validateDeletableStatus();
        timeDeal.validateDeletablePeriod(LocalDateTime.now());

        timeDeal.softDelete();

        eventPublisher.publishEvent(new TimeDealStockDecreaseEventReq(
                timeDeal.getTimeDealId(),
                timeDeal.getProductId(),
                timeDeal.getRemainingQuantity()
        ));

        log.info("타임딜 삭제 완료");
    }

    @Override
    @Transactional
    public void decreaseRemainingQuantity(DecreaseRemainingQuantityCommand command) {
        log.info("타임딜 남은 수량 차감 시작");

        TimeDeal timeDeal = getActiveTimeDeal(command.timeDealId());

        timeDeal.decreaseRemainingQuantity(command.decreaseQuantity());

        log.info("타임딜 남은 수량 차감 완료");
    }

    @Override
    @Transactional
    public void restoreRemainingQuantity(RestoreRemainingQuantityCommand command) {
        log.info("타임딜 남은 수량 복구 시작");

        TimeDeal timeDeal = getActiveTimeDeal(command.timeDealId());

        if (timeDeal.isClosed()) {
            eventPublisher.publishEvent(new TimeDealStockDecreaseEventReq(
                    timeDeal.getTimeDealId(),
                    timeDeal.getProductId(),
                    timeDeal.getRemainingQuantity()
            ));
        } else {
            timeDeal.restoreRemainingQuantity(command.restoreQuantity());
        }

        log.info("타임딜 남은 수량 복구 성공");
    }

    @Override
    @Transactional
    public void deleteByCompanyUser(UUID companyUserId) {
        log.info("업체 판매자 관련 타임딜 삭제 시작");

        LocalDateTime now = LocalDateTime.now();

        List<TimeDeal> timeDeals = timeDealRepository.findAllByCompanyUserId(companyUserId, TimeDealStatus.PENDING, now);

        for (TimeDeal timeDeal : timeDeals) {
            if (!timeDeal.isDeletable(now)) {
                continue;
            }

            timeDeal.softDelete();

            eventPublisher.publishEvent(new TimeDealStockDecreaseEventReq(
                    timeDeal.getTimeDealId(),
                    timeDeal.getProductId(),
                    timeDeal.getRemainingQuantity()
            ));
        }

        log.info("업체 판매자 관련 타임딜 삭제 완료");
    }

    // TODO. 로직 수정 필요
    private void updateTimeDealFields(TimeDeal timeDeal, UpdateTimeDealCommand command) {
        TimeDealStatus status = timeDeal.getTimeDealStatus();

        if (!command.title().equals(timeDeal.getTitle())) {
            timeDealValidator.validateEditable(status, TimeDealEditableField.TITLE);
            timeDeal.changeTitle(command.title());
        }

        if (!command.description().equals(timeDeal.getDescription())) {
            timeDealValidator.validateEditable(status, TimeDealEditableField.DESCRIPTION);
            timeDeal.changeDescription(command.description());
        }

        if (!command.startAt().equals(timeDeal.getPeriod().getStartAt())) {
            timeDealValidator.validateEditable(status, TimeDealEditableField.START_AT);
            timeDeal.changeStartAt(command.startAt());
        }

        if (!command.endAt().equals(timeDeal.getPeriod().getEndAt())) {
            timeDealValidator.validateEditable(status, TimeDealEditableField.END_AT);
            timeDeal.changeEndAt(command.endAt());
        }

        if (command.timeDealPrice() != timeDeal.getAmount().getTimeDealPrice()) {
            timeDealValidator.validateEditable(status, TimeDealEditableField.TIME_DEAL_PRICE);
            timeDeal.changeTimeDealPrice(command.timeDealPrice());
        }

        if (command.totalQuantity() != timeDeal.getTimeDealStock().getQuantity().getTotalQuantity()) {
            timeDealValidator.validateEditable(status, TimeDealEditableField.TOTAL_QUANTITY);
            timeDeal.changeTotalQuantity(command.totalQuantity());
        }
    }

    private TimeDeal getActiveTimeDeal(UUID timeDealId) {
        return timeDealRepository.findByTimeDealId(timeDealId)
                .orElseThrow(() -> new BusinessException(CommonErrorCode.NOT_FOUND));
    }

    private void validateCompanyUser(UserRole role, UUID ownerCompanyUserId) {
        if (role == UserRole.COMPANY_USER) {
            timeDealValidator.validateCompanyUserId(ownerCompanyUserId);
        }
    }
}
