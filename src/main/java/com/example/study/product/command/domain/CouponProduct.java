package com.example.study.product.command.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.util.List;

@Audited
@Entity
@Getter
@DiscriminatorValue("COUPON")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponProduct extends Product {
    private int discountPrice;
    private int effectiveDay;

    public CouponProduct(String name, int price, int stockQuantity, String description, String productStatus, List<ProductTag> productTags,
                         int discountPrice, int effectiveDay){
        super(name, price, stockQuantity, description);
        super.setProductStatus(productStatus);
        for(ProductTag productTag : productTags){
            super.setProductTags(new ProductTag(productTag.getTagName()));
        }
        this.discountPrice = discountPrice;
        this.effectiveDay = effectiveDay;
    }

    public static CouponProduct fromProduct(Product product, int discountPrice, int effectiveDay) {
        return new CouponProduct(
                product.getName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getDescription(),
                product.getProductStatus().name(),
                product.getProductTags(),
                discountPrice,
                effectiveDay
        );
    }

    public void setDiscountPrice(int discountPrice){
        this.discountPrice = discountPrice;
    }

    public void setEffectiveDay(int effectiveDay){
        this.effectiveDay = effectiveDay;
    }
}
