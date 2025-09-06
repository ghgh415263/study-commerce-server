package com.example.study.product.command.domain.product;

import com.example.study.product.command.application.product.InvalidProductParameterException;

/**
 * 상품의 상태를 나타내는 열거형입니다.
 */
public enum ProductStatus {
    /** 판매중 상태의 상품 */
    ON_SALE,

    /** 품절 상태의 상품 */
    SOLD_OUT;

    public static ProductStatus from(String value) {
        for (ProductStatus state : values()) {
            if (state.name().equalsIgnoreCase(value)) {
                return state;
            }
        }
        throw new InvalidProductParameterException("Unknown ProductStatus: " + value);
    }
}
