package com.palja.review_service.domain.repository;

import com.palja.review_service.domain.entity.Review;

public interface ReviewRepository {

    Review save(Review review);
}
