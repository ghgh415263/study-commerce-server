package com.example.study.product.command.domain.product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    Optional<DeliveryProduct> findByDeliveryProductId(Long id);

    Optional<CouponProduct> findByCouponProductId(Long id);

    void delete(Product product);

    List<Product> findAllByIds(List<Long> ids);
}
