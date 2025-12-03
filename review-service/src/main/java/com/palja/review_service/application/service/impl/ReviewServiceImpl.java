package com.palja.review_service.application.service.impl;

import com.palja.review_service.application.command.CreateReviewCommand;
import com.palja.review_service.application.dto.res.CreateReviewRes;
import com.palja.review_service.application.dto.res.FindProductReviewsRes;
import com.palja.review_service.application.dto.res.FindReviewRes;
import com.palja.review_service.application.service.ReviewService;
import com.palja.review_service.domain.entity.Review;
import com.palja.review_service.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository repository;

    @Override
    public CreateReviewRes create(CreateReviewCommand createCommand) {

        /**
         * TODO : 로그인 아이디로 리뷰를 작성한 회원의 ID(Long)와 이름을 찾아와야함.
         * TODO : 주문 아이디로 주문서비스에서 상품의 ID를 찾아와야함
         */
        //UserDto userDto = UserClient.getDto(createCommand.loginId());
        //UUID orderProductId = OrderClient.getProductId(createCommand.orderId());
        Long userId = 1L;
        UUID orderProductId = UUID.fromString("703d287b-f39f-4587-8fcb-465bcd61c1fc");
        String userName = "홍*동";
        Review review = Review.create(createCommand.title(),
                createCommand.content(),
                createCommand.rating(),
                createCommand.isLike(),
                createCommand.disLike(),
                userId,
                userName,
                createCommand.orderId(),
                orderProductId);

        Review saved = repository.save(review);
        return CreateReviewRes.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public FindReviewRes findReview(UUID reviewId) {

        Review review = repository.findReview(reviewId);

        return FindReviewRes.fromEntity(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FindProductReviewsRes> findProductReviews(UUID productId, Pageable pageable) {

        List<Review> productReviews = repository.findProductReviews(productId, pageable);
        List<FindProductReviewsRes> content = productReviews.stream().map(FindProductReviewsRes::fromEntity).toList();

        return new PageImpl<>(content, pageable, productReviews.size());
    }
}
