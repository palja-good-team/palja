package com.palja.review_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "p_review")
@Getter
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "review_id")
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(scale = 1, precision = 2)
    private BigDecimal rating;

    @Column(nullable = false, name = "is_like")
    private Boolean like;

    @Column(nullable = false, name = "is_dislike")
    private Boolean dislike;

    @Column(nullable = false, name = "user_id")
    private Long userId;

    @Column(nullable = false, name = "user_name")
    private String userName;

    @Column(nullable = false, name = "order_id")
    private UUID orderId;

    @Column(nullable = false, name = "product_id")
    private UUID productId;

    protected Review() {}

    public static Review create(String title, String content, BigDecimal rating, Boolean isLike, Boolean dislike, Long userId, String userName, UUID orderId, UUID productId) {
        Review review = new Review();

        review.title = title;
        review.content = content;
        review.rating = rating;
        review.like = isLike;
        review.dislike = dislike;
        review.userId = userId;
        review.userName = userName;
        review.orderId = orderId;
        review.productId = productId;

        return review;
    }
}
