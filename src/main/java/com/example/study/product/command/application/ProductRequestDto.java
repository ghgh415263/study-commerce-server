package com.example.study.product.command.application;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProductRequestDto(

        @NotBlank(message = "상품 이름은 필수입니다.")
        String name,

        @NotNull
        @Min(value = 1, message = "가격은 최소 {value} 이상이어야 합니다.")
        Integer price,

        @NotNull
        @Min(value = 1, message = "수량은 최소 {value} 이상이어야 합니다.")
        Integer stockQuantity,

        String description,

        @NotBlank(message = "상품 상태값은 필수 입니다.")
        String productStatus,

        List<ProductTagDto> productTags,

        @NotBlank(message = "상품 타입은 필수 입니다.")
        String productType,

        DeliveryProductDto deliveryProduct,

        CouponProductDto couponProduct
) {}
