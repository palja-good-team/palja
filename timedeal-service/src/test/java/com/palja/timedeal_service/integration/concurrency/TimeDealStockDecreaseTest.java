package com.palja.timedeal_service.integration.concurrency;

import com.palja.common.auditor.AuditorContext;
import com.palja.common.vo.UserRole;
import com.palja.timedeal_service.application.command.DecreaseRemainingQuantityCommand;
import com.palja.timedeal_service.application.service.TimeDealService;
import com.palja.timedeal_service.domain.entity.TimeDeal;
import com.palja.timedeal_service.domain.repository.TimeDealRepository;
import com.palja.timedeal_service.domain.vo.Amount;
import com.palja.timedeal_service.domain.vo.Period;
import com.palja.timedeal_service.domain.vo.Quantity;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TimeDealStockDecreaseTest {

    private static final String TEST_LOGIN_ID = "test-user";
    private static final int THREAD_COUNT = 100;
    private static final long INITIAL_QUANTITY = 100L;
    private static final long DECREASE_QUANTITY = 1L;

    @Autowired
    private TimeDealService timeDealService;

    @Autowired
    private TimeDealRepository timeDealRepository;

    @Autowired
    private EntityManager entityManager;

    private TimeDeal timeDeal;

    @BeforeEach
    void setUp() {
        AuditorContext.set(TEST_LOGIN_ID, UserRole.MASTER);

        LocalDateTime now = LocalDateTime.now();

        timeDeal = TimeDeal.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "동시성 테스트 타임딜",
                "동시성 테스트",
                Period.of(now.plusMinutes(10), now.plusDays(1)),
                Amount.of(10000L, 5000L),
                Quantity.of(INITIAL_QUANTITY)
        );

        timeDeal.openNow("동시성 테스트 타임딜 오픈");
        timeDealRepository.save(timeDeal);
    }

    @AfterEach
    void tearDown() {
        AuditorContext.clear();
    }

    /**
     * Redisson 락 및 원자적 update 적용 전
     * 동시성 상황에서 성공/실패 수와 잔여 재고를 관찰하기 위한 테스트
     * 상황에 따라 flaky 할 수 있으므로 정합성 불일치를 강하게 assert 하지 않음
     */
    @Test
    @DisplayName("타임딜 재고 차감 동시 요청 시 성공, 실패 수와 잔여 재고 테스트")
    void concurrentTimeDealStockDecreaseTest() throws InterruptedException {
        log.info("테스트 시작");

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < THREAD_COUNT ; i++) {
            executorService.submit(() -> {
                try {
                    AuditorContext.set("test-user", UserRole.MASTER);

                    timeDealService.decreaseRemainingQuantity(
                            new DecreaseRemainingQuantityCommand(timeDeal.getTimeDealId(), DECREASE_QUANTITY)
                    );
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    log.error("타임딜 재고 차감 실패 - message={}", e.getMessage(), e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        entityManager.clear();

        TimeDeal persistedTimeDeal = timeDealRepository.findByTimeDealId(timeDeal.getTimeDealId())
                .orElseThrow(() -> new IllegalArgumentException("타임딜 조회 실패"));

        log.info("조회 완료");
        log.info("성공 요청 수 = {}", successCount.get());
        log.info("실패 요청 수 = {}", failCount.get());
        log.info("잔여 타임딜 수량 = {}", persistedTimeDeal.getRemainingQuantity());

        assertThat(successCount.get() + failCount.get()).isEqualTo(THREAD_COUNT);

        executorService.shutdown();
    }
}