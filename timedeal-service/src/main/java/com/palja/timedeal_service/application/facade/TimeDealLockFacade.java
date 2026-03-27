package com.palja.timedeal_service.application.facade;

import com.palja.timedeal_service.application.command.DecreaseRemainingQuantityCommand;
import com.palja.timedeal_service.application.service.TimeDealService;
import com.palja.timedeal_service.application.port.LockExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeDealLockFacade {

    private static final String LOCK_KEY_PREFIX = "lock:timedeal:decrease:";

    private final LockExecutor distributedLockExecutor;
    private final TimeDealService timeDealService;

    public void decreaseRemainingQuantityWithLock(DecreaseRemainingQuantityCommand command) {
        String lockKey = LOCK_KEY_PREFIX + command.timeDealId();

        distributedLockExecutor.execute(
                lockKey,
                1,
                TimeUnit.SECONDS,
                () -> {
                    log.info("타임딜 재고 차감 락 획득 성공. timeDealId={}", command.timeDealId());
                    timeDealService.decreaseRemainingQuantity(command);
                });
    }
}
