package com.palja.order_service.infrastructure.repository;

import com.palja.order_service.domain.entity.QOrder;
import com.palja.order_service.domain.entity.QOrderDelivery;
import com.palja.order_service.domain.entity.QOrderItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueryDslOrderRepository {
    private final JPAQueryFactory queryFactory;

    private static final QOrder order = QOrder.order;
    private static final QOrderItem orderItem = QOrderItem.orderItem;
    private static final QOrderDelivery delivery = QOrderDelivery.orderDelivery;
}