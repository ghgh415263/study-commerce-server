package com.example.study.product.command.infra;

import com.example.study.product.command.domain.product.CouponProduct;
import com.example.study.product.command.domain.product.DeliveryProduct;
import com.example.study.product.command.domain.product.Product;
import com.example.study.product.command.domain.product.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Product.class, id, LockModeType.PESSIMISTIC_WRITE));
    }

    @Override
    public Optional<DeliveryProduct> findByDeliveryProductId(Long id) {
        return Optional.ofNullable(entityManager.find(DeliveryProduct.class, id, LockModeType.PESSIMISTIC_WRITE));
    }

    @Override
    public Optional<CouponProduct> findByCouponProductId(Long id) {
        return Optional.ofNullable(entityManager.find(CouponProduct.class, id, LockModeType.PESSIMISTIC_WRITE));
    }

    @Override
    public void delete(Product product){
        entityManager.remove(product);
    }

    @Override
    public List<Product> findAllByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        return entityManager.createQuery(
                        "SELECT p FROM Product p WHERE p.id IN :ids", Product.class)
                .setParameter("ids", ids)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE) // 비관적 락
                .getResultList();
    }
}
