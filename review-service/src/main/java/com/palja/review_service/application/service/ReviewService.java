package com.palja.review_service.application.service;

import com.palja.review_service.application.command.CreateReviewCommand;
import com.palja.review_service.application.dto.res.CreateReviewRes;
import com.palja.review_service.application.dto.res.FindProductReviewsRes;
import com.palja.review_service.application.dto.res.FindReviewRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReviewService {

    CreateReviewRes create(CreateReviewCommand createCommand);

    FindReviewRes findReview(UUID reviewId);

    Page<FindProductReviewsRes> findProductReviews(UUID productId, Pageable pageable);
}
