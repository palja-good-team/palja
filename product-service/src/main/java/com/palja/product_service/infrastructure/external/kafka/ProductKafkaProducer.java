package com.palja.product_service.infrastructure.external.kafka;

import com.palja.product_service.application.event.ChangePriceEvent;
import com.palja.product_service.application.event.ProductEvent;
import com.palja.product_service.application.event.publisher.ProductEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.RetriableException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductKafkaProducer implements ProductEventPublisher {

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Integer MAX_RETRIES = 3;

    private void publish(String topic, ProductEvent productEvent, int retries) {

        CompletableFuture<SendResult<String, ProductEvent>> send = kafkaTemplate.send(topic, productEvent);
        send.whenComplete((result, exception) -> {

            if(result != null) {
                log.info("{}에 대한 메세지 전송 성공", result.getRecordMetadata().topic());
                return;
            }
            if (exception instanceof RetriableException && retries < MAX_RETRIES) {
                executor.execute(() -> retry(topic, productEvent, retries));
            }
            else {
                log.error("{}에 대한 메세지 전송 실패 - 이유 = {}", topic, exception.getMessage());
            }
        });
    }

    private void retry(String topic, ProductEvent productEvent, int retryCount) {
        log.info("{}에 대한 메세지 재전송", topic);
        publish(topic, productEvent, retryCount + 1);
    }

    @Override
    public void handleChangePriceEvent(ChangePriceEvent event) {
        publish(ProductKafkaTopic.CHANGE_PRODUCT_PRICE.getTopicName(), event, 0);
    }
}
