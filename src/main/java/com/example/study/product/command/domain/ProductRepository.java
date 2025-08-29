package com.example.study.product.command.domain;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findByProductId(Long id);

    Optional<DeliveryProduct> findByDeliveryProductId(Long id);

    Optional<CouponProduct> findByCouponProductId(Long id);

    void delete(Product product);
}
