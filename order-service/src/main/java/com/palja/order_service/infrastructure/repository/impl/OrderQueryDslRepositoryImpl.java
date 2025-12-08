package com.palja.order_service.infrastructure.repository.impl;

import com.palja.order_service.domain.entity.QOrder;
import com.palja.order_service.domain.entity.QOrderDelivery;
import com.palja.order_service.domain.entity.QOrderItem;
import com.palja.order_service.infrastructure.repository.OrderQueryDslRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

// Order Repository QueryDSL 구현체
@Repository
@RequiredArgsConstructor
public class OrderQueryDslRepositoryImpl implements OrderQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    private static final QOrder order = QOrder.order;
    private static final QOrderItem orderItem = QOrderItem.orderItem;
    private static final QOrderDelivery delivery = QOrderDelivery.orderDelivery;
}