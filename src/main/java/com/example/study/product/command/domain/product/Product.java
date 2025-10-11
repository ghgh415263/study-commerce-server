package com.example.study.product.command.domain.product;

import com.example.study.common.persistance.BaseUpdateEntity;
import com.example.study.product.command.application.product.InvalidProductParameterException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Audited
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorColumn(name = "product_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Product extends BaseUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stockQuantity;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus = ProductStatus.SOLD_OUT;

    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<ProductTag> productTags = new ArrayList<>();

    // 읽기 전용
    @Column(name = "product_type", insertable = false, updatable = false)
    private String productType;

    public Product(String name, int price, int stockQuantity, String description, String productStatus) {
        this.name = name;
        assignPrice(price);
        assignStockQuantity(stockQuantity);
        this.description = description;
        this.productStatus = ProductStatus.from(productStatus);
    }

    public void update(String name, int price, int stockQuantity, String description, String productStatus) {
        this.name = name;
        assignPrice(price);
        assignStockQuantity(stockQuantity);
        this.description = description;
        this.productStatus = ProductStatus.from(productStatus);
    }

    public void assignProductTags(ProductTag tags) {
        this.productTags.add(tags);
        tags.assignProduct(this);
    }

    @Transactional
    public void updateProductTags(Product product, List<ProductTag> newTags) {
        product.getProductTags().clear();
        newTags.forEach(product::assignProductTags);
    }

    public void assignPrice(int price) {
        if (price < 0) {
            throw new InvalidProductParameterException("price는 음수가 될 수 없습니다.");
        }
        this.price = price;
    }

    public void assignStockQuantity(int stockQuantity) {
        if (stockQuantity < 0) {
            throw new InvalidProductParameterException("stockQuantity는 음수가 될 수 없습니다.");
        }
        this.stockQuantity = stockQuantity;
    }
}
