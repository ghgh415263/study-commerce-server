package com.example.study.order.order.command.domain;

import com.example.study.common.persistance.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@Getter
@Entity
@Audited
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryItem extends BaseUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private Long orderItemId;

    public DeliveryItem(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }
}
