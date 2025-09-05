package com.example.study.review.command.application;

import com.example.study.common.Utils.StringUtils;
import com.example.study.review.command.domain.Review;
import lombok.Getter;

@Getter
public class ReviewResponseDto {
    /* 리뷰 기본 */
    private Long productId;
    private String loginId;
    private String content;
    private int star;

    public static ReviewResponseDto from(Review review) {
        ReviewResponseDto dto = new ReviewResponseDto();
        dto.productId = review.getProductId();
        dto.loginId = StringUtils.maskId(review.getLoginId());
        dto.content = review.getContent();
        dto.star = review.getStar();
        return dto;
    }
}
