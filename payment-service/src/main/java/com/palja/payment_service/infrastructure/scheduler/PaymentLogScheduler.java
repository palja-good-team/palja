package com.palja.payment_service.infrastructure.scheduler;

import com.palja.payment_service.application.service.PaymentLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentLogScheduler {

    private final PaymentLogService paymentLogService;

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteOldPaymentLogs() {
        try{
            log.info("결제 로그 자동 삭제 스케줄러 시작");
            paymentLogService.deleteOldLogs();
            log.info("결제 로그 자동 삭제 스케줄러 완료");
        } catch (Exception e) {
            log.error("결제 로그 자동 삭제 중 오류 발생",e);
        }
    }
}
