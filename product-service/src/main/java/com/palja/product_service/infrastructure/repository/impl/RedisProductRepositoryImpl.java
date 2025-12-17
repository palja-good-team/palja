package com.palja.product_service.infrastructure.repository.impl;

import com.palja.product_service.domain.repository.RedisProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisProductRepositoryImpl implements RedisProductRepository {

    private final RedissonClient redissonClient;

    @Value("${redis-key.map0}")
    private String hashKey0;

    @Value("${redis-key.map1}")
    private String hashKey1;

    @Value("${redis-key.time-suffix}")
    private String timeSuffix;

    @Override
    public boolean decreaseStockBySale(String productId, Long stock, Long quantity) {

        //상품의 아이디의 이름으로 락을 건다.
        RLock lock = redissonClient.getLock(productId);
        String hashKey = createRedisHashKey(productId);
        RTransaction transaction = null;

        try {
            //락을 10초동안 얻지 못한다면 실패 반환
            if (!lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                return false;
            }

            //Redisson에서 지원하는 트랜잭션
            transaction = redissonClient.createTransaction(
                    TransactionOptions.defaults().timeout(10, TimeUnit.SECONDS)
            );

            // 레디스에 key로 매핑된 Hash(자바의 Map)을 가져온다.
            RMap<String, Long> map = transaction.getMap(hashKey);

            //값이 있으면 가져오고 없으면 DB의 재고로 잡는다
            Long remainStock = map.getOrDefault(productId, stock);

            //남아있는 재고보다 판매수량이 많다면 예외
            Long resultStock = remainStock - quantity;
            if (resultStock < 0) {
                throw new InterruptedException();
            }

            //레디스에 새로운 재고를 넣고, DB 재고차감 스케줄링에 사용할 값을 넣는다.
            map.fastPut(productId, resultStock);
            setTime(hashKey+timeSuffix, productId);

            //예외 없이 모든 작업이 끝난다면 커밋해 레디스에 적용시킨다
            transaction.commit();

        } catch (Exception e) {
            if(Objects.nonNull(transaction))
                transaction.rollback();
            return false;
        } finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public boolean adjustStock(String productId, Long quantity) {

        RLock lock = redissonClient.getLock(productId);
        String hashKey = createRedisHashKey(productId);

        try {
            //락을 10초동안 얻지 못한다면 실패 반환
            if (!lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                return false;
            }

            RMap<String, Long> map = redissonClient.getMap(hashKey);

            //DB에 먼저 값이 저장되고 레디스에 저장하는 방식이기 때문에, 덮어씌워야함
            map.fastPut(productId, quantity);

        } catch (InterruptedException e) {
            return false;
        } finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public boolean deleteProductStock(String productId) {

        RLock lock = redissonClient.getLock(productId);
        String hashKey = createRedisHashKey(productId);
        RTransaction transaction = null;

        try {
            //락을 10초동안 얻지 못한다면 실패 반환
            if (!lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                return false;
            }

            transaction = redissonClient.createTransaction(
                    TransactionOptions.defaults().timeout(10, TimeUnit.SECONDS));

            RMap<String, Integer> map = transaction.getMap(hashKey);
            RScoredSortedSet<String> set = redissonClient.getScoredSortedSet(hashKey+timeSuffix);

            map.fastRemove(productId);
            set.remove(productId);

            transaction.commit();

        } catch (Exception e) {
            if(Objects.nonNull(transaction))
                transaction.rollback();
            return false;
        } finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public boolean deleteAllStockFromRedis(List<UUID> productIds) {

        for (UUID productId : productIds) {
            RLock lock = redissonClient.getLock(productId.toString());

            try {
                if (!lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                    continue;
                }

                RMap<String, Integer> map =
                        redissonClient.getMap(createRedisHashKey(productId.toString()));

                map.fastRemove(productId.toString());

            } catch (Exception e) {
                log.error("Please Retry Delete this Id = {}", productId);
            }
        }
        return true;
    }

    protected String createRedisHashKey(String productId) {

        String substring = productId.substring(0, 8);
        int hash = substring.hashCode();
        if(hash % 2 == 0)
            return hashKey0;
        else return hashKey1;
    }

    private void setTime(String setKey, String productId) {

        long score = LocalDateTime.now().plusMinutes(1).toEpochSecond(ZoneOffset.UTC);
        RScoredSortedSet<String> set = redissonClient.getScoredSortedSet(setKey);

        set.add(score, productId);
    }
}
