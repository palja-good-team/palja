package com.palja.review_service.presentation.controller;

import com.palja.common.auditor.AuditorContext;
import com.palja.common.response.ApiResponse;
import com.palja.review_service.application.command.CreateReviewCommand;
import com.palja.review_service.application.dto.res.CreateReviewRes;
import com.palja.review_service.application.service.ReviewService;
import com.palja.review_service.presentation.dto.req.CreateReviewReq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService service;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateReviewRes>> createReview(@RequestBody @Valid CreateReviewReq createReq) {

        String loginId = AuditorContext.get().getLoginId();
        if(createReq.getIsLike().equals(Boolean.TRUE) && createReq.getIsLike().equals(createReq.getDisLike()))
            throw new IllegalArgumentException();

        CreateReviewCommand createCommand = createReq.toCommand(loginId);

        CreateReviewRes res = service.create(createCommand);

        return new ResponseEntity<>(ApiResponse.success(res, "리뷰 생성 성공"), HttpStatus.OK);
    }
}
