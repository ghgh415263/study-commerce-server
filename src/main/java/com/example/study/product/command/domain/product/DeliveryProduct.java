package com.example.study.product.command.domain.product;

import com.example.study.product.command.application.product.InvalidProductParameterException;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

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
        assignFee(fee);
        assignWeight(weight);
    }

    public void assignFee(int fee){
        if (fee < 0) {
            throw new InvalidProductParameterException("배송 비용은 0원 이하일 수 없습니다.");
        }
        this.fee = fee;
    }

    public void assignWeight(int weight){
        if (weight <= 0) {
            throw new InvalidProductParameterException("무게는 0보다 커야합니다.");
        }
        this.weight = weight;
    }
}
