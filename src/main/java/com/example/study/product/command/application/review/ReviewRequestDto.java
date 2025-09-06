package com.example.study.product.command.application.review;

import jakarta.validation.constraints.*;

public record ReviewRequestDto (
        @NotNull(message = "id는 필수입니다.")
        @Positive(message = "id는 0이나 음수가 될 수 없습니다.")
        Long productId,

        @NotBlank(message = "리뷰 내용은 필수 입니다.")
        String content,

        @NotNull
        @Min(value = 1, message = "별점은 최소 {value} 점 입니다.")
        @Max(value = 10, message = "별점은 최대 {value} 점 입니다.")
        Integer star
){}
