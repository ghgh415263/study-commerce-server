package com.example.study.unit;

import com.example.study.order.order.command.domain.Delivery;
import com.example.study.order.order.command.domain.DeliveryStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public class DeliveryTest {

    @Test
    @DisplayName("배송 아이템 추가 테스트")
    void addDeliveryItems_success() {
        Delivery delivery = new Delivery("서울 동작구 사당로", "01012345678", 1L);

        delivery.addDeliveryItems(List.of(10L, 11L, 12L));

        Assertions.assertEquals(3, delivery.getDeliveryItems().size());
        Assertions.assertEquals(10L, delivery.getDeliveryItems().get(0).getOrderItemId());
    }

    @Test
    @DisplayName("NOT_STARTED 또는 READY 상태에서만 cancel() 가능")
    void cancel_success() {
        Delivery delivery = new Delivery("서울 동작구 사당로", "01012345678", 1L);

        delivery.addDeliveryItems(List.of(1L));

        delivery.cancel();

        Assertions.assertEquals(DeliveryStatus.CANCELED, delivery.getStatus());
    }
}
