package com.example.study.product.command.application.product;

import jakarta.validation.constraints.NotBlank;

public record ProductTagDto(
        @NotBlank(message = "상품 태그 이름은 필수입니다.")
        String tagName
){}
