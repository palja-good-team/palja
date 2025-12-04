package com.palja.review_service.infrastructure.repository;

import com.palja.review_service.domain.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findAllByProductId(UUID productId, Pageable pageable);
}
