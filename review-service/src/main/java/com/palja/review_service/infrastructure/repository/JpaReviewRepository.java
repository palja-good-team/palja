package com.palja.review_service.infrastructure.repository;

import com.palja.review_service.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaReviewRepository extends JpaRepository<Review, UUID> {
}
