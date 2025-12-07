package com.palja.payment_service.infrastructure.scheduler;

import com.palja.common.exception.BusinessException;
import com.palja.payment_service.application.service.PaymentLogService;
import com.palja.payment_service.exception.PaymentErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class PaymentLogSchedulerTest {

    @Mock
    private PaymentLogService paymentLogService;

    @InjectMocks
    private PaymentLogScheduler paymentLogScheduler;

    @Test
    @DisplayName("스케줄러 실행 시 deleteOldLogs 호출 확인")
    void deleteOldPaymentLogs_success() {
        paymentLogScheduler.deleteOldPaymentLogs();

        then(paymentLogService).should().deleteOldLogs();
    }

    @Test
    @DisplayName("스케줄러 실행 시 deleteOldLogs에서 예외 발생해도 정상 처리")
    void deleteOldPaymentLogs_exceptionHandled() {
        doThrow(new BusinessException(PaymentErrorCode.PAYMENT_LOG_NOT_FOUND))
                .when(paymentLogService).deleteOldLogs();

        paymentLogScheduler.deleteOldPaymentLogs();

        then(paymentLogService).should().deleteOldLogs();
    }
}
