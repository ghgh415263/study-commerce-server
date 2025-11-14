package com.example.study.order.order.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<DeliveryAddress, Long> {
    Delivery save(Delivery delivery);
}
