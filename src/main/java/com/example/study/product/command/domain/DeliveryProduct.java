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
@DiscriminatorValue("DELIVERY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryProduct extends Product {
    private int fee;
    private int weight;

    public DeliveryProduct(String name, int price, int stockQuantity, String description, String productStatus,
                           int fee, int weight) {
        super(name, price, stockQuantity, description, productStatus);
        validateNegative(Map.of(
                "fee", fee,
                "weight", weight
        ));
        this.fee = fee;
        this.weight = weight;
    }

    public void assignFee(int fee){
        this.fee = fee;
    }

    public void assignWeight(int weight){
        this.weight = weight;
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
