package com.palja.order_service.infrastructure.external.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * DLT 모니터링 Consumer
 * - DLT로 전송된 메시지 감지
 * - 로깅 및 추적
 */
@Slf4j
@Component
public class DltMonitoringConsumer {

    @KafkaListener(
            topics = {
                    "saga.start.request.DLT",
                    "order.stock.decrease.success.DLT",
                    "order.stock.decrease.failure.DLT",
                    "order.coupon.use.success.DLT",
                    "order.coupon.use.failure.DLT",
                    "order.payment.create.success.DLT",
                    "order.payment.create.failure.DLT"
            },
            groupId = "order-service-dlt-monitor"
    )
    public void monitorDlt(ConsumerRecord<String, Object> record) {

        String dltTopic = record.topic();
        String originalTopic = stripDltSuffix(dltTopic);

        String exceptionClass = header(record, org.springframework.kafka.support.KafkaHeaders.DLT_EXCEPTION_FQCN);
        String exceptionMsg   = header(record, org.springframework.kafka.support.KafkaHeaders.DLT_EXCEPTION_MESSAGE);

        log.error("[KAFKA][ORDER][DLT][CONSUMED] dltTopic={} originalTopic={} key={} partition={} offset={} exceptionClass={} exceptionMsg={}",
                dltTopic, originalTopic, record.key(), record.partition(), record.offset(),
                exceptionClass, exceptionMsg);
    }

    private String stripDltSuffix(String topic) {
        return (topic != null && topic.endsWith(".DLT")) ? topic.substring(0, topic.length() - 4) : topic;
    }

    private String header(ConsumerRecord<String, Object> record, String key) {
        if (record == null || record.headers() == null) return null;
        var h = record.headers().lastHeader(key);
        return (h == null || h.value() == null) ? null : new String(h.value(), java.nio.charset.StandardCharsets.UTF_8);
    }
}