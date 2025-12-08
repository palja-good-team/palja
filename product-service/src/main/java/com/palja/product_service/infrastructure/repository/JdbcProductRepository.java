package com.palja.product_service.infrastructure.repository;

import com.palja.product_service.domain.dto.req.StockScheduleDto;
import com.palja.product_service.infrastructure.event.RedisTimeSetClearEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JdbcProductRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void stockBulkUpdateForSchedule(Collection<StockScheduleDto> dtos) {

        String sql = """
                UPDATE palja_product.p_product_stock ps 
                SET quantity = ? 
                FROM palja_product.p_product p 
                WHERE ps.product_id = p.product_id AND ps.product_id = ?
                """;
        jdbcTemplate.batchUpdate(
                sql, dtos, dtos.size(),
                (ps, dto) -> {
                    ps.setInt(1, dto.getQuantity());
                    ps.setObject(2, dto.getProductId());
                }
        );

        eventPublisher.publishEvent(new RedisTimeSetClearEvent());
    }
}
