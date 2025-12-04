package com.palja.payment_service.infrastructure.repository;

import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.entity.QPayment;
import com.palja.payment_service.domain.repository.PaymentRepository;
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
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(UUID id){
        return paymentJpaRepository.findById(id);
    }

    @Override
    public Page<Payment> findAll(PageRequest pageRequest) {
        return paymentJpaRepository.findAll(pageRequest);
    }

    @Override
    public Page<Payment> findPayments(PaymentStatus status, Long userId, UUID orderId, LocalDateTime startDate, LocalDateTime endDate, PageRequest pageRequest) {
        QPayment payment = QPayment.payment;

        BooleanBuilder builder = new BooleanBuilder();

        if (status != null) {
            builder.and(payment.status.eq(status));
        }
        if (userId != null) {
            builder.and(payment.userId.eq(userId));
            /*
            TODO: 권한별로 userID 조회 다르게 (일반 사용자는 본인것만 볼 수 있게)
             */
        }
        if (orderId != null) {
            builder.and(payment.orderId.eq(orderId));
            /*
            TODO: 권한별로 orderID 조회 다르게 (일반 사용자는 본인것만 볼 수 있게)
             */
        }
        if (startDate != null) {
            builder.and(payment.requestedAt.goe(startDate));
        }
        if (endDate != null) {
            builder.and(payment.requestedAt.loe(endDate));
        }

        QueryResults<Payment> queryResults = queryFactory
                .selectFrom(payment)
                .where(builder)
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetchResults();

        return new PageImpl<>(queryResults.getResults(), pageRequest, queryResults.getTotal());
    }
}
