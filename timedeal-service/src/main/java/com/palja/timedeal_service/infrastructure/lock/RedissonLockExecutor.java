package com.palja.timedeal_service.infrastructure.lock;

import com.palja.common.exception.BusinessException;
import com.palja.timedeal_service.application.port.LockExecutor;
import com.palja.timedeal_service.common.TimeDealErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockExecutor implements LockExecutor {

    private final RedissonClient redissonClient;

    @Override
    public void execute(String key, long waitTime, TimeUnit timeUnit, Runnable task) {
        RLock lock = redissonClient.getLock(key);

        boolean locked = false;

        try {
            locked = lock.tryLock(waitTime, timeUnit);

            if (!locked) {
                log.error("분산락 획득 실패. key={}", key);
                throw new BusinessException(TimeDealErrorCode.LOCK_ACQUIRE_FAILED);
            }

            task.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("분산락 대기 중 인터럽트 발생. key={}", key, e);
            throw new BusinessException(TimeDealErrorCode.LOCK_INTERRUPTED);
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("분산락 해제 완료. key={}", key);
            }
        }
    }
}
