package com.palja.review_service.application.service.impl;

import com.palja.review_service.application.command.CreateReviewCommand;
import com.palja.review_service.application.dto.res.CreateReviewRes;
import com.palja.review_service.application.dto.res.FindReviewRes;
import com.palja.review_service.domain.entity.Review;
import com.palja.review_service.domain.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    private Review review;
    private CreateReviewCommand createCommand;

    @BeforeEach
    void init() {
        UUID orderId = UUID.randomUUID();
        createCommand = new CreateReviewCommand(
                "리뷰", "내용", BigDecimal.ONE, Boolean.TRUE, Boolean.FALSE, orderId, "loginId"
        );

        Long userId = 1L;
        String userName = "홍*동";
        UUID productId = UUID.randomUUID();
        review = Review.create(createCommand.title(),
                createCommand.content(),
                createCommand.rating(),
                createCommand.isLike(),
                createCommand.disLike(),
                userId,
                userName,
                createCommand.orderId(),
                productId);
    }

    @Test
    @DisplayName("리뷰 생성에 성공한다")
    void create() {
        //given
        CreateReviewCommand command = createCommand;
        Review repositoryResult = review;

        CreateReviewRes expected = CreateReviewRes.fromEntity(repositoryResult);
        given(reviewRepository.save(any(Review.class))).willReturn(repositoryResult);

        //when
        CreateReviewRes result = reviewService.create(command);

        //then
        assertThat(result.getTitle()).isEqualTo(expected.getTitle());
        assertThat(result.getContent()).isEqualTo(expected.getContent());
        assertThat(result.getRating()).isEqualTo(expected.getRating());
        assertThat(result.getLike()).isEqualTo(expected.getLike());
        assertThat(result.getDisLike()).isEqualTo(expected.getDisLike());
    }

    @Test
    @DisplayName("리뷰 단건 조회에 성공한다")
    void findReview() {
        //given
        Review repositoryResult = review;
        UUID ReviewId = UUID.randomUUID();
        given(reviewRepository.findReview(ReviewId)).willReturn(repositoryResult);

        FindReviewRes expected = FindReviewRes.fromEntity(repositoryResult);

        //when
        FindReviewRes result = reviewService.findReview(ReviewId);

        //then
        assertThat(result.getTitle()).isEqualTo(expected.getTitle());
        assertThat(result.getContent()).isEqualTo(expected.getContent());
        assertThat(result.getRating()).isEqualTo(expected.getRating());
        assertThat(result.getLike()).isEqualTo(expected.getLike());
        assertThat(result.getDisLike()).isEqualTo(expected.getDisLike());
        assertThat(result.getUserName()).isEqualTo(expected.getUserName());
    }
}