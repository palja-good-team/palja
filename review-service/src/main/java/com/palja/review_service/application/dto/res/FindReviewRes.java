package com.palja.review_service.application.dto.res;

import com.palja.review_service.domain.entity.Review;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class FindReviewRes {

    private UUID reviewId;
    private String title;
    private String content;
    private BigDecimal rating;
    private Boolean like;
    private Boolean disLike;
    private String userName;

    public static FindReviewRes fromEntity(Review review) {
        FindReviewRes res = new FindReviewRes();

        res.reviewId = review.getId();
        res.title = review.getTitle();
        res.content = review.getContent();
        res.rating = review.getRating();
        res.like = review.getLike();
        res.disLike = review.getDislike();
        res.userName = review.getUserName();

        return res;
    }
}
