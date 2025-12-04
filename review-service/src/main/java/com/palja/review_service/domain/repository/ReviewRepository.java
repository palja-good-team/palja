package com.palja.review_service.domain.repository;

import com.palja.review_service.domain.entity.Review;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository {

    Review save(Review review);

    Review findReview(UUID reviewId);

    List<Review> findProductReviews(UUID productId, Pageable pageable);
}
