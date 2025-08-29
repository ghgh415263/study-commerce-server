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
@DiscriminatorValue("DELIVERY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryProduct extends Product {
    private int fee;
    private int weight;

    public DeliveryProduct(String name, int price, int stockQuantity, String description, String productStatus, List<ProductTag> productTags,
                           int fee, int weight) {
        super(name, price, stockQuantity, description);
        super.setProductStatus(productStatus);
        for(ProductTag productTag : productTags){
            super.setProductTags(new ProductTag(productTag.getTagName()));
        }
        this.fee = fee;
        this.weight = weight;
    }

    public static DeliveryProduct fromProduct(Product product, int fee, int weight) {
        return new DeliveryProduct(
                product.getName(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getDescription(),
                product.getProductStatus().name(),
                product.getProductTags(),
                fee,
                weight
        );
    }

    public void setFee(int fee){
        this.fee = fee;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }

}
