package com.palja.review_service.application.service;

import com.palja.review_service.application.command.CreateReviewCommand;
import com.palja.review_service.application.dto.res.CreateReviewRes;

public interface ReviewService {

    CreateReviewRes create(CreateReviewCommand createCommand);
}
