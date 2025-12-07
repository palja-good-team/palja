package com.palja.payment_service.infrastructure.repository;

import com.palja.payment_service.domain.entity.PaymentLog;
import com.palja.payment_service.domain.entity.QPaymentLog;
import com.palja.payment_service.domain.vo.PaymentStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentLogQueryDSLRepositoryImpl {

    private final JPAQueryFactory queryFactory;

    public Page<PaymentLog> findLogs(
            UUID paymentId,
            UUID orderId,
            PaymentStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            PageRequest pageRequest
    ){
        QPaymentLog paymentLog = QPaymentLog.paymentLog;
        BooleanBuilder builder = new BooleanBuilder();

        if (paymentId != null) {
            builder.and(paymentLog.payment.id.eq(paymentId));
        }
        if (orderId != null) {
            builder.and(paymentLog.orderId.eq(orderId));
        }
        if (status != null) {
            builder.and(paymentLog.status.eq(status));
        }
        if (startDate != null) {
            builder.and(paymentLog.processedAt.goe(startDate));
        }
        if (endDate != null) {
            builder.and(paymentLog.processedAt.loe(endDate));
        }

        QueryResults<PaymentLog> results = queryFactory
                .selectFrom(paymentLog)
                .where(builder)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageRequest, results.getTotal());
    }
}
