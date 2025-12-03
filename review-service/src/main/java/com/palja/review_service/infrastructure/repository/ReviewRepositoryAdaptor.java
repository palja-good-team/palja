package com.palja.review_service.infrastructure.repository;

import com.palja.review_service.domain.entity.Review;
import com.palja.review_service.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReviewRepositoryAdaptor implements ReviewRepository {

    private final JpaReviewRepository jpaReviewRepository;

    @Override
    public Review save(Review review) {
        return jpaReviewRepository.save(review);
    }

    @Override
    public Review findReview(UUID reviewId) {
        return jpaReviewRepository.findById(reviewId).orElseThrow();
    }
}
