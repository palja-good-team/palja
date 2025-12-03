package com.palja.review_service.application.service.impl;

import com.palja.review_service.application.command.CreateReviewCommand;
import com.palja.review_service.application.dto.res.CreateReviewRes;
import com.palja.review_service.application.service.ReviewService;
import com.palja.review_service.domain.entity.Review;
import com.palja.review_service.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository repository;

    @Override
    public CreateReviewRes create(CreateReviewCommand createCommand) {

        /**
         * TODO : 로그인 아이디로 리뷰를 작성한 회원의 ID(Long)를 찾아와야함.
         * TODO : 주문 아이디로 주문서비스에서 상품의 ID를 찾아와야함
         */
        //Long userId = UserClient.getId(createCommand.loginId());
        //UUID orderProductId = OrderClient.getProductId(createCommand.orderId());
        Long userId = 1L;
        UUID orderProductId = UUID.randomUUID();
        Review review = Review.create(createCommand.title(),
                createCommand.content(),
                createCommand.rating(),
                createCommand.isLike(),
                createCommand.disLike(),
                userId,
                createCommand.orderId(),
                orderProductId);

        Review saved = repository.save(review);
        return  CreateReviewRes.fromEntity(saved);
    }
}
