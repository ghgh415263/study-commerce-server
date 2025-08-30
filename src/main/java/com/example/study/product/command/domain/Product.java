package com.example.study.product.command.domain;

import com.example.study.common.persistance.BaseUpdateEntity;
import com.example.study.member.command.domain.InvalidMemberStateException;
import com.example.study.member.command.domain.MemberStatus;
import com.example.study.product.command.application.InvalidProductColumnException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public Product(String name, int price, int stockQuantity, String description, String productStatus) {
        validateNegative(Map.of(
                "price", price,
                "stockQuantity", stockQuantity
        ));
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.productStatus = ProductStatus.from(productStatus);
    }

    public void update(String name, int price, int stockQuantity, String description, String productStatus) {
        validateNegative(Map.of(
                "price", price,
                "stockQuantity", stockQuantity
        ));
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
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

    /**
     * 엔티티 음수 체크
     * @param columns
     */
    private void validateNegative(Map<String, Integer> columns) {
        for (Map.Entry<String, Integer> entry : columns.entrySet()) {
            if (entry.getValue() < 0) {
                throw new InvalidProductColumnException(entry.getKey());
            }
        }
    }
}
