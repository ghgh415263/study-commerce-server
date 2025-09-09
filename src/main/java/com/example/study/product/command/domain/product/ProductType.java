package com.example.study.product.command.domain.product;

/**
 * 상품의 종류를 나타내는 열거형입니다.
 */
public enum ProductType {
    /** 배송형 상품 */
    DELIVERY,

    /** 쿠폰 상품 */
    COUPON;

    public static ProductType from(String value) {
        for (ProductType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
