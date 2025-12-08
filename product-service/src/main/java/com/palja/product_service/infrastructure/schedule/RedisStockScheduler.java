package com.palja.product_service.infrastructure.schedule;

import com.palja.product_service.domain.dto.req.StockScheduleDto;
import com.palja.product_service.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.IntegerCodec;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CompositeCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Component
@RequiredArgsConstructor
public class RedisStockScheduler {

    private final RedissonClient redissonClient;
    private final ProductRepository productRepository;

    @Value("${redis-key.map0}")
    private String map0Key;

    @Value("${redis-key.map1}")
    private String map1Key;

    @Value("${redis-key.time-suffix}")
    private String timeSuffix;

    @Scheduled(cron = "0 */5 * * * *")
    public void updateDbStock() {

        RMap<String, Integer> map0 = redissonClient.getMap(
                map0Key, new CompositeCodec(StringCodec.INSTANCE, IntegerCodec.INSTANCE));
        RMap<String, Integer> map1 = redissonClient.getMap(
                map1Key, new CompositeCodec(StringCodec.INSTANCE, IntegerCodec.INSTANCE));
        RScoredSortedSet<String> set0 = redissonClient.getScoredSortedSet(map0Key+timeSuffix);
        RScoredSortedSet<String> set1 = redissonClient.getScoredSortedSet(map1Key+timeSuffix);
        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

        //2개의 Set에서 과거 ~ 현재 시간까지의 값들만 가져옴
        Collection<String> targetIds = set0.valueRange(Double.MIN_VALUE, true, now, true);
        targetIds.addAll(set1.valueRange(Double.MIN_VALUE, true, now, true));

        //재고정보가 저장된 map에서 위에서 필터링된 값듦만 가져옴
        Map<String, Integer> idStock = map0.getAll(new HashSet<>(targetIds));
        idStock.putAll(map1.getAll(new HashSet<>(targetIds)));

        //레포지토리에 요청을 보낼 데이터로 가공
        Set<StockScheduleDto> set = new HashSet<>();
        idStock.forEach((k,v)-> set.add(new StockScheduleDto(UUID.fromString(k), v)));

        productRepository.stockBulkUpdateForSchedule(set);
    }
}
