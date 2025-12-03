package com.palja.review_service.application.service;

import com.palja.review_service.application.command.CreateReviewCommand;
import com.palja.review_service.application.dto.res.CreateReviewRes;
import com.palja.review_service.application.dto.res.FindReviewRes;

import java.util.UUID;

public interface ReviewService {

    CreateReviewRes create(CreateReviewCommand createCommand);

    FindReviewRes findReview(UUID reviewId);
}
