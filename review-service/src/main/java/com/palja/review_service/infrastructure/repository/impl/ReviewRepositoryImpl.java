package com.palja.review_service.infrastructure.repository.impl;

import com.palja.common.exception.BusinessException;
import com.palja.review_service.domain.entity.Review;
import com.palja.review_service.domain.repository.ReviewRepository;
import com.palja.review_service.exception.ReviewErrorCode;
import com.palja.review_service.infrastructure.repository.JpaReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {

    private final JpaReviewRepository jpaReviewRepository;

    @Override
    public Review save(Review review) {
        return jpaReviewRepository.save(review);
    }

    @Override
    public List<Review> findProductReviews(UUID productId, Pageable pageable) {
        return jpaReviewRepository.findAllByProductId(productId, pageable);
    }

    @Override
    public Review findReview(UUID reviewId) {
        return jpaReviewRepository.findById(reviewId).orElseThrow(
                () -> new BusinessException(ReviewErrorCode.NOT_FOUND_REVIEW)
        );
    }

}
