package com.example.study.product.command.domain;

import com.example.study.common.persistance.BaseUpdateEntity;
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
@DiscriminatorValue("PRODUCT")
public class Product extends BaseUpdateEntity {

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

    public Product(String name, int price, int stockQuantity, String description) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
    }

    public void update(String name, int price, int stockQuantity, String description, String productStatus) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.setProductStatus(productStatus);
    }

    public void setProductTags(ProductTag tags) {
        this.productTags.add(tags);
        tags.setProduct(this);
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = ProductStatus.from(productStatus);
    }

    @Transactional
    public void updateProductTags(Product product, List<ProductTag> newTags) {
        product.getProductTags().clear();
        newTags.forEach(product::setProductTags);
    }
}
