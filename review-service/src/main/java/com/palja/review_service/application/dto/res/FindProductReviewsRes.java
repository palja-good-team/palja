package com.palja.review_service.application.dto.res;

import com.palja.review_service.domain.entity.Review;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class FindProductReviewsRes {

    private UUID reviewId;
    private String title;
    private String content;
    private BigDecimal rating;
    private String userName;

    public static FindProductReviewsRes fromEntity(Review review) {
        FindProductReviewsRes res = new FindProductReviewsRes();

        res.reviewId = review.getId();
        res.title = review.getTitle();
        res.content = review.getContent();
        res.rating = review.getRating();
        res.userName = review.getUserName();

        return res;
    }
}
