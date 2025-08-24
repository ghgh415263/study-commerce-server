package com.example.study.product.command.infra;

import com.example.study.product.command.domain.CouponProduct;
import com.example.study.product.command.domain.DeliveryProduct;
import com.example.study.product.command.domain.Product;
import com.example.study.product.command.domain.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JpaProductRepository implements ProductRepository{

    private final EntityManager entityManager;

    @Override
    public Product save(Product product){
        if (product.getId() == null) {
            entityManager.persist(product);
            return product;
        } else {
            return entityManager.merge(product);
        }
    }

    @Override
    public Optional<Product> findByProductId(Long id) {
        return Optional.ofNullable(entityManager.find(Product.class, id));
    }

    @Override
    public Optional<DeliveryProduct> findByDeliveryProductId(Long id) {
        return Optional.ofNullable(entityManager.find(DeliveryProduct.class, id));
    }

    @Override
    public Optional<CouponProduct> findByCouponProductId(Long id) {
        return Optional.ofNullable(entityManager.find(CouponProduct.class, id));
    }

    @Override
    public void delete(Product product){
        entityManager.remove(product);
    }

}
