package com.palja.review_service.domain.repository;

import com.palja.review_service.domain.entity.Review;

import java.util.UUID;

public interface ReviewRepository {

    Review save(Review review);

    Review findReview(UUID reviewId);
}
