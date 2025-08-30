package com.example.study.product.command.domain;

import com.example.study.product.command.application.InvalidProductColumnException;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.util.List;
import java.util.Map;

@Audited
@Entity
@Getter
@DiscriminatorValue("COUPON")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponProduct extends Product {
    private int discountPrice;
    private int effectiveDay;

    public CouponProduct(String name, int price, int stockQuantity, String description, String productStatus,
                         int discountPrice, int effectiveDay){
        super(name, price, stockQuantity, description, productStatus);
        validateNegative(Map.of(
                "discountPrice", discountPrice,
                "effectiveDay", effectiveDay
        ));
        this.discountPrice = discountPrice;
        this.effectiveDay = effectiveDay;
    }

    public void assignDiscountPrice(int discountPrice){
        this.discountPrice = discountPrice;
    }

    public void assignEffectiveDay(int effectiveDay){
        this.effectiveDay = effectiveDay;
    }

    /**
     * 엔티티 음수 체크
     * @param columns
     */
    private void validateNegative(Map<String, Integer> columns) {
        for (Map.Entry<String, Integer> entry : columns.entrySet()) {
            if (entry.getValue() < 0) {
                throw new InvalidProductColumnException(entry.getKey());
            }
        }
    }
}
