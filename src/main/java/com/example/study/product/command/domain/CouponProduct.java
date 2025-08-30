package com.example.study.product.command.domain;

import com.example.study.product.command.application.InvalidProductParameterException;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

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
        assignDiscountPrice(discountPrice);
        assignEffectiveDay(effectiveDay);
    }

    public void assignDiscountPrice(int discountPrice){
        if (discountPrice < 0) {
            throw new InvalidProductParameterException("할인가격은 음수일 수 없습니다.");
        }
        this.discountPrice = discountPrice;
    }

    public void assignEffectiveDay(int effectiveDay){
        if (effectiveDay < 0) {
            throw new InvalidProductParameterException("사용기간은 음수일 수 없습니다.");
        }
        this.effectiveDay = effectiveDay;
    }

}
