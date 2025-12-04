package com.palja.review_service.presentation.dto.req;

import com.palja.review_service.application.command.CreateReviewCommand;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewReq {

    @NotEmpty(message = "리뷰의 제목은 필수항목 입니다.")
    private String title;

    @NotEmpty(message = "리뷰의 내용은 필수항목 입니다.")
    private String content;

    @NotNull(message = "리뷰의 별점은 필수항목 입니다")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    private BigDecimal rating;

    private Boolean isLike;

    private Boolean disLike;

    @NotNull
    private UUID orderId;

    public CreateReviewCommand toCommand(String loginId) {
        return new CreateReviewCommand(title, content, rating, isLike, disLike, orderId, loginId);
    }
}
