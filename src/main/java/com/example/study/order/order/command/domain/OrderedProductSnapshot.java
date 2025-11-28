package com.example.study.order.order.command.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderedProductSnapshot {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private String productType;

    private OrderedProductSnapshot(String name, BigDecimal price, String productType) {
        this.name = name;
        this.price = price;
        this.productType = productType;
    }

    public static OrderedProductSnapshot of(String name, BigDecimal price, String productType) {

        if (name == null || name.isBlank())
            throw new IllegalArgumentException("이름은 필수입니다.");

        if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");

        if (productType == null || productType.isBlank())
            throw new IllegalArgumentException("productType은 필수입니다.");

        return new OrderedProductSnapshot(name, price, productType);
    }
}