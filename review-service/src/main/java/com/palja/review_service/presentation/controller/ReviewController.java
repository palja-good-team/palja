package com.palja.review_service.presentation.controller;

import com.palja.common.auditor.AuditorContext;
import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.review_service.application.command.CreateReviewCommand;
import com.palja.review_service.application.dto.res.CreateReviewRes;
import com.palja.review_service.application.dto.res.FindProductReviewsRes;
import com.palja.review_service.application.dto.res.FindReviewRes;
import com.palja.review_service.application.service.ReviewService;
import com.palja.review_service.presentation.dto.req.CreateReviewReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService service;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateReviewRes>> createReview(@RequestBody @Valid CreateReviewReq createReq) {
        System.out.println("adfsdf");
        String loginId = AuditorContext.get().getLoginId();
        if(createReq.getIsLike().equals(Boolean.TRUE) && createReq.getIsLike().equals(createReq.getDisLike()))
            throw new IllegalArgumentException();

        CreateReviewCommand createCommand = createReq.toCommand(loginId);

        CreateReviewRes res = service.create(createCommand);

        return new ResponseEntity<>(ApiResponse.success(res, "리뷰 생성 성공"), HttpStatus.CREATED);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<FindReviewRes>> findReview(@PathVariable UUID reviewId) {

        FindReviewRes res = service.findReview(reviewId);

        return new ResponseEntity<>(ApiResponse.success(res, "리뷰 조회 성공"),  HttpStatus.OK);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<PageResponse<FindProductReviewsRes>>> findProductReviews(@PathVariable UUID productId,
                                                                                               Pageable pageable) {

        Page<FindProductReviewsRes> res = service.findProductReviews(productId, pageable);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(PageResponse.from(res), "상품의 리뷰들 조회 성공"));
    }
}
