package com.palja.product_service.infrastructure.repository;

import com.palja.product_service.domain.dto.req.FindListByConditionReq;
import com.palja.product_service.domain.dto.res.FindProductListByConditionDto;
import com.palja.product_service.domain.dto.res.ProductInfoForOrderDto;
import com.palja.product_service.domain.dto.res.ProductInfoForTimeDealDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.palja.product_service.domain.entity.QProduct.product;

@Component
@RequiredArgsConstructor
public class DslProductRepository {

    private final JPAQueryFactory queryFactory;

    public ProductInfoForTimeDealDto findProductForTimeDeal(UUID productId) {

        return queryFactory.select(Projections.constructor(
                        ProductInfoForTimeDealDto.class,
                        product.id,
                        product.companyUserId,
                        product.price.amount.longValue(),
                        product.productStock.quantity.longValue()))
                .from(product)
                .where(product.id.eq(productId).and(product.deletedAt.isNull()))
                .fetchOne();
    }

    public ProductInfoForOrderDto findProductForOrder(UUID productId) {

        return queryFactory.select(Projections.constructor(
                        ProductInfoForOrderDto.class,
                        product.id,
                        product.companyUserId,
                        product.name,
                        product.price.amount,
                        product.productStock.quantity))
                .from(product)
                .where(product.id.eq(productId).and(product.deletedAt.isNull()))
                .fetchOne();
    }

    public List<FindProductListByConditionDto> findProductByCondition(FindListByConditionReq condition,
                                                                      long offset, int limit) {

        return queryFactory
                .select(Projections.constructor(
                        FindProductListByConditionDto.class,
                        product.id,
                        product.name,
                        product.description,
                        product.price,
                        product.category,
                        product.avgRating
                ))
                .from(product)
                .where(nameLike(condition.getName()),
                        priceBetween(condition.getMinPrice(), condition.getMaxPrice()),
                        categoryEq(condition.getCategory()),
                        ratingBetween(condition.getMinRating(), condition.getMaxRating()))
                .offset(offset)
                .limit(limit)
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
        return name != null ? product.name.contains(name) : null;
    }

    private BooleanExpression priceBetween(Long minPrice, Long maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return null;
        }
        if (minPrice == null) {
            return product.price.amount.loe(BigDecimal.valueOf(maxPrice));
        }
        if (maxPrice == null) {
            return product.price.amount.goe(BigDecimal.valueOf(minPrice));
        }
        return product.price.amount.between(
                BigDecimal.valueOf(minPrice), BigDecimal.valueOf(maxPrice));
    }

    private BooleanExpression categoryEq(String category) {
//        return category != null ? product.category.eq(Category.fromString(category)) : null;
        return null;
    }

    private BooleanExpression ratingBetween(BigDecimal minRating, BigDecimal maxRating) {
        if (minRating == null && maxRating == null)
            return null;

        if (minRating == null)
            return product.avgRating.loe(maxRating);

        if (maxRating == null)
            return product.avgRating.goe(minRating);

        return product.avgRating.between(minRating, maxRating);
    }
}
