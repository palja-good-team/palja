package com.palja.timedeal_service.infrastructure.external.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.timedeal_service.application.command.DecreaseRemainingQuantityCommand;
import com.palja.timedeal_service.application.command.RestoreRemainingQuantityCommand;
import com.palja.timedeal_service.application.event.dto.KafkaEvent;
import com.palja.timedeal_service.application.event.dto.request.in.TimeDealStockDecreaseEventReq;
import com.palja.timedeal_service.application.event.dto.request.in.TimeDealStockRestoreEventReq;
import com.palja.timedeal_service.application.event.dto.response.TimeDealStockDecreaseEventRes;
import com.palja.timedeal_service.application.service.TimeDealService;
import com.palja.timedeal_service.infrastructure.external.kafka.topic.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class TimeDealKafkaConsumer {

    private final TimeDealService timeDealService;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = KafkaTopics.ORDER_STOCK_DECREASE_REQUEST)
    public void timeDealStockDecrease(ConsumerRecord<String, Object> record) {
        TimeDealStockDecreaseEventReq event = objectMapper.convertValue(record.value(), TimeDealStockDecreaseEventReq.class);

        if (!isValidTimeDealEvent(event.isTimeDeal())) {
            log.info("[Kafka] 재고 차감 요청 스킵. orderId={}, isTimeDeal={}", event.getOrderId(), event.isTimeDeal());
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
    public void timeDealStockRestore(ConsumerRecord<String, Object> record) {
        TimeDealStockRestoreEventReq event = objectMapper.convertValue(record.value(), TimeDealStockRestoreEventReq.class);

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

        log.info("[Kafka] 주문 재고 복구 처리 완료. sagaId={}, orderId={}", event.getSagaId(), event.getOrderId());
    }

    private boolean isValidTimeDealEvent(boolean isTimeDeal) {
        return isTimeDeal;
    }
}
