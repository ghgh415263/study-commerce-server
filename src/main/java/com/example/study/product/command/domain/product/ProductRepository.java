package com.example.study.product.command.domain.product;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

/**
 * 비관락이므로 서비스 단계에서 @DBLockTimeout 이 같이 선언되어야 한다.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DeliveryProduct> findDeliveryProductById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<CouponProduct> findCouponProductById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findProductById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Product> findAllByIdIn(List<Long> ids);

}
