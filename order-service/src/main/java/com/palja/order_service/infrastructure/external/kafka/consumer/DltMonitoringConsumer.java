package com.palja.order_service.infrastructure.external.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * DLT 모니터링 Consumer
 *
 * - DLT로 전송된 메시지를 감지
 * - 정상 플로우가 아님을 ERROR 로그로 명확히 기록
 * - 운영/장애 대응을 위한 추적 정보만 남김
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

        String exceptionClass = header(record, KafkaHeaders.DLT_EXCEPTION_FQCN);
        String exceptionMsg   = header(record, KafkaHeaders.DLT_EXCEPTION_MESSAGE);

        log.error("DLT 메시지 수신 감지 (dead letter consumed):" +
                        " dltTopic={} originalTopic={} key={} partition={} offset={} exceptionClass={} exceptionMsg={}",
                dltTopic, originalTopic, record.key(), record.partition(), record.offset(),
                exceptionClass, exceptionMsg);
    }

    private String stripDltSuffix(String topic) {
        return (topic != null && topic.endsWith(".DLT"))
                ? topic.substring(0, topic.length() - 4)
                : topic;
    }

    private String header(ConsumerRecord<String, Object> record, String key) {
        if (record == null || record.headers() == null) {
            return "N/A";
        }
        var header = record.headers().lastHeader(key);
        if (header == null || header.value() == null) {
            return "N/A";
        }
        return new String(header.value(), StandardCharsets.UTF_8);
    }
}