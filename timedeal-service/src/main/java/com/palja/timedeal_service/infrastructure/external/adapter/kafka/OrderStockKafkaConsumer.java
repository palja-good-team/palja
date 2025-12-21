package com.palja.timedeal_service.infrastructure.external.adapter.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.timedeal_service.application.command.DecreaseRemainingQuantityCommand;
import com.palja.timedeal_service.application.command.RestoreRemainingQuantityCommand;
import com.palja.timedeal_service.application.event.order.request.StockDecreaseEventReq;
import com.palja.timedeal_service.application.event.order.request.StockRestoreEventReq;
import com.palja.timedeal_service.application.event.order.response.StockDeductEventRes;
import com.palja.timedeal_service.application.service.TimeDealService;
import com.palja.timedeal_service.infrastructure.config.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStockKafkaConsumer {

    private final TimeDealService timeDealService;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = KafkaTopics.ORDER_STOCK_DEDUCT_REQUEST)
    public void deduct(ConsumerRecord<String, Object> record) {
        StockDecreaseEventReq event = objectMapper.convertValue(record.value(), StockDecreaseEventReq.class);

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

            StockDeductEventRes res = StockDeductEventRes.success(event.getSagaId(), event.getOrderId());
            kafkaTemplate.send(KafkaTopics.ORDER_STOCK_DEDUCT_SUCCESS, res);

            log.info("[Kafka] 주문 재고 차감 성공 응답 발행 sagaId={}, orderId={}", event.getSagaId(), event.getOrderId());
        } catch (Exception e) {
            StockDeductEventRes res = StockDeductEventRes.failure(event.getSagaId(), event.getOrderId());
            kafkaTemplate.send(KafkaTopics.ORDER_STOCK_DEDUCT_FAILURE, res);

            log.error("[Kafka] 주문 재고 차감 실패 응답 발행 sagaId={}, orderId={}", event.getSagaId(), event.getOrderId(),e);
        }
    }

    @KafkaListener(topics = KafkaTopics.ORDER_STOCK_RESTORE_REQUEST)
    public void restore(ConsumerRecord<String, Object> record) {
        StockRestoreEventReq event = objectMapper.convertValue(record.value(), StockRestoreEventReq.class);

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
