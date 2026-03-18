package com.palja.timedeal_service.infrastructure.external.kafka.consumer;

import com.palja.timedeal_service.application.command.ChangeTimeDealStatusFailedCommand;
import com.palja.timedeal_service.application.command.DecreaseRemainingQuantityCommand;
import com.palja.timedeal_service.application.command.RestoreRemainingQuantityCommand;
import com.palja.timedeal_service.application.event.dto.TimeDealEvent;
import com.palja.timedeal_service.application.event.dto.request.in.ProductStockDecreaseFailureEventReq;
import com.palja.timedeal_service.application.event.dto.request.in.TimeDealDeleteByCompanyUserEventReq;
import com.palja.timedeal_service.application.event.dto.request.in.TimeDealStockDecreaseEventReq;
import com.palja.timedeal_service.application.event.dto.request.in.TimeDealStockRestoreEventReq;
import com.palja.timedeal_service.application.event.dto.response.TimeDealStockDecreaseEventRes;
import com.palja.timedeal_service.application.service.TimeDealService;
import com.palja.timedeal_service.infrastructure.external.kafka.topic.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class TimeDealKafkaConsumer {

    private final TimeDealService timeDealService;
    private final KafkaTemplate<String, TimeDealEvent> kafkaTemplate;

    @KafkaListener(topics = KafkaTopics.ORDER_STOCK_DECREASE_REQUEST)
    public void timeDealStockDecrease(TimeDealStockDecreaseEventReq event) {
        if (!isValidTimeDealEvent(event.isTimeDeal())) {
            log.info("[Kafka] 재고 차감 요청 스킵 orderId={}, isTimeDeal={}", event.getOrderId(), event.isTimeDeal());
            return;
        }

        log.info("[Kafka] 주문 재고 차감 요청 수신 orderId={}, timeDealId={}, quantity={}", event.getOrderId(), event.getTimeDealId(), event.getQuantity());

        try {
            DecreaseRemainingQuantityCommand command = DecreaseRemainingQuantityCommand.builder()
                    .timeDealId(event.getTimeDealId())
                    .decreaseQuantity(event.getQuantity())
                    .build();

            timeDealService.decreaseRemainingQuantity(command);

            TimeDealStockDecreaseEventRes res = TimeDealStockDecreaseEventRes.success(event.getSagaId(), event.getOrderId());
            kafkaTemplate.send(KafkaTopics.ORDER_STOCK_DECREASE_SUCCESS, res);

            log.info("[Kafka] 주문 재고 차감 성공 응답 발행 sagaId={}, orderId={}", event.getSagaId(), event.getOrderId());
        } catch (Exception e) {
            TimeDealStockDecreaseEventRes res = TimeDealStockDecreaseEventRes.failure(event.getSagaId(), event.getOrderId());
            kafkaTemplate.send(KafkaTopics.ORDER_STOCK_DECREASE_FAILURE, res);

            log.error("[Kafka] 주문 재고 차감 실패 응답 발행 sagaId={}, orderId={}", event.getSagaId(), event.getOrderId(),e);
        }
    }

    @KafkaListener(topics = KafkaTopics.ORDER_STOCK_RESTORE_REQUEST)
    public void timeDealStockRestore(TimeDealStockRestoreEventReq event) {
        if (!isValidTimeDealEvent(event.isTimeDeal())) {
            log.info("[Kafka] 주문 재고 복구 요청 스킵 orderId={}, isTimeDeal={}", event.getOrderId(), event.isTimeDeal());
            return;
        }

        log.info("[Kafka] 주문 재고 복구 요청 수신 orderId={}, timeDealId={}, quantity={}", event.getOrderId(), event.getTimeDealId(), event.getQuantity());


        RestoreRemainingQuantityCommand command = RestoreRemainingQuantityCommand.builder()
                .timeDealId(event.getTimeDealId())
                .restoreQuantity(event.getQuantity())
                .build();

        timeDealService.restoreRemainingQuantity(command);

        log.info("[Kafka] 주문 재고 복구 처리 완료 sagaId={}, orderId={}", event.getSagaId(), event.getOrderId());
    }

    @KafkaListener(topics = KafkaTopics.USER_COMPANY_USER_DELETE_REQUEST)
    public void timeDealDelete(TimeDealDeleteByCompanyUserEventReq event) {
        log.info("[Kafka] 업체 판매자 관련 타임딜 삭제 요청 수신 companyUserId={}", event.getCompanyUserId());

        timeDealService.deleteByCompanyUser(event.getCompanyUserId());

        log.info("[Kafka] 업체 판매자 관련 타임딜 삭제 요청 완료 companyUserId={}", event.getCompanyUserId());
    }

    private boolean isValidTimeDealEvent(boolean isTimeDeal) {

        return isTimeDeal;
    }

    @KafkaListener(topics = KafkaTopics.PRODUCT_STOCK_DECREASE_FAILURE)
    public void timeDealStatusFailed(ProductStockDecreaseFailureEventReq event) {
        log.info("[Kafka] 상품 재고 차감 실패 이벤트 수신 timeDealId = {}, productId={}, message={}", event.getTimeDealId(), event.getProductId(), event.getMessage());

        ChangeTimeDealStatusFailedCommand command = ChangeTimeDealStatusFailedCommand.builder()
                .timeDealId(event.getTimeDealId())
                .productId(event.getProductId())
                .reason(event.getMessage())
                .build();

        timeDealService.changeTimeDealStatusFailed(command);

        log.info("[Kafka] 타임딜 실패 보상 처리 완료 timeDealId={}", event.getTimeDealId());
    }
}
