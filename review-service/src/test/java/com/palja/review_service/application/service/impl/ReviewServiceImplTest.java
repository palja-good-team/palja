package com.palja.review_service.application.service.impl;

import com.palja.review_service.application.command.CreateReviewCommand;
import com.palja.review_service.application.dto.res.CreateReviewRes;
import com.palja.review_service.domain.entity.Review;
import com.palja.review_service.domain.repository.ReviewRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("리뷰 생성에 성공한다")
    void create() {
        //given
        UUID orderId = UUID.randomUUID();
        Long userId = 1L;
        UUID orderProductIdId = UUID.randomUUID();
        CreateReviewCommand createCommand = new CreateReviewCommand(
                "리뷰", "내용", BigDecimal.ONE, Boolean.TRUE, Boolean.FALSE, orderId, "loginId"
        );
        Review review = Review.create(createCommand.title(),
                createCommand.content(),
                createCommand.rating(),
                createCommand.isLike(),
                createCommand.disLike(),
                userId,
                createCommand.orderId(),
                orderProductIdId);

        CreateReviewRes expected = CreateReviewRes.fromEntity(review);

        given(reviewRepository.save(any(Review.class))).willReturn(review);

        //when
        CreateReviewRes result = reviewService.create(createCommand);

        //then
        assertThat(result.getTitle()).isEqualTo(expected.getTitle());
        assertThat(result.getContent()).isEqualTo(expected.getContent());
        assertThat(result.getRating()).isEqualTo(expected.getRating());
        assertThat(result.getLike()).isEqualTo(expected.getLike());
        assertThat(result.getDisLike()).isEqualTo(expected.getDisLike());
    }
}