package com.palja.product_service.infrastructure.repository;

import com.palja.product_service.application.dto.res.ProductInfoForTimeDealRes;
import com.palja.product_service.domain.dto.req.FindListByConditionReq;
import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.vo.Category;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.palja.product_service.domain.entity.QProduct.product;

@Component
@RequiredArgsConstructor
public class DslProductRepository {

    private final JPAQueryFactory queryFactory;

    public ProductInfoForTimeDealRes findProductForTimeDeal(UUID productId) {

        return queryFactory.select(Projections.constructor(
                        ProductInfoForTimeDealRes.class,
                        product.id,
                        product.companyUserId,
                        product.price.amount.longValue(),
                        product.productStock.quantity.longValue()))
                .from(product)
                .where(product.id.eq(productId).and(product.deletedAt.isNull()))
                .fetchOne();
    }

    public List<Product> findProductByCondition(FindListByConditionReq condition, Pageable pageable) {

        return queryFactory
                .select(product)
                .from(product)
                .join(product.productStock).fetchJoin()
                .where(nameLike(condition.getName()),
                        priceBetween(condition.getMinPrice(), condition.getMaxPrice()),
                        categoryEq(condition.getCategory()),
                        ratingBetween(condition.getMinRating(), condition.getMaxRating()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public Long getPageCount(FindListByConditionReq condition) {
        return queryFactory.select(product.count())
                .from(product)
                .where(nameLike(condition.getName()),
                        priceBetween(condition.getMinPrice(), condition.getMaxPrice()),
                        categoryEq(condition.getCategory()),
                        ratingBetween(condition.getMinRating(), condition.getMaxRating()))
                .fetchOne();
    }

    private BooleanExpression nameLike(String name) {
        return name != null ? product.name.like("%" + name + "%") : null;
    }

    private BooleanExpression priceBetween(Long minPrice, Long maxPrice) {
        return product.price.amount.between(
                BigDecimal.valueOf(minPrice), BigDecimal.valueOf(maxPrice));
    }

    private BooleanExpression categoryEq(Category category) {
        return product.category.eq(category);
    }

    private BooleanExpression ratingBetween(BigDecimal minRating, BigDecimal maxRating) {
        return product.avgRating.between(minRating, maxRating);
    }
}
