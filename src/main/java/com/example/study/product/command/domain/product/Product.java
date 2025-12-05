package com.example.study.product.command.domain.product;

import com.example.study.common.persistance.BaseUpdateEntity;
import com.example.study.product.command.application.product.InvalidProductParameterException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.util.*;

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

    @Column(nullable = false, unique = true)
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

    //ElementCollection 은 기본이 LAZY
    @ElementCollection
    @CollectionTable(name = "product_tag", joinColumns = @JoinColumn(name = "product_id"))
    private Set<ProductTag> productTags = new HashSet<>();

    // 읽기 전용
    @Column(name = "product_type", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    public Product(String name, int price, int stockQuantity, String description, String productStatus) {
        this.name = name;
        assignPrice(price);
        if (stockQuantity < 0) throw new InvalidProductParameterException("stockQuantity는 음수가 될 수 없습니다.");
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.productStatus = ProductStatus.from(productStatus);
    }

    public void update(String name, int price, String description, String productStatus) {
        this.name = name;
        assignPrice(price);
        this.description = description;
        this.productStatus = ProductStatus.from(productStatus);
    }

    public void assignProductTags(List<ProductTag> tags) {
        checkDuplicateProductTags(tags);

        List<ProductTag> duplicates = tags.stream()
                .filter(pt -> this.productTags.contains(pt))
                .toList();

        if (!duplicates.isEmpty()) {
            throw new DuplicateProductTagsException(duplicates);
        }

        this.productTags.addAll(tags);
    }

    public void updateProductTags(List<ProductTag> newTags) {
        this.productTags.clear();
        assignProductTags(newTags);
    }

    public void assignPrice(int price) {
        if (price < 0) {
            throw new InvalidProductParameterException("price는 음수가 될 수 없습니다.");
        }
        this.price = price;
    }

    public void decreaseStock(int amount) {
        if (this.stockQuantity - amount < 0) {
            throw new InvalidProductParameterException("stockQuantity는 음수가 될 수 없습니다.");
        }
        this.stockQuantity -= amount;
    }

    public void increaseStock(int amount) {
        if (amount < 0) {
            throw new InvalidProductParameterException("재고 증가량은 음수일 수 없습니다.");
        }

        this.stockQuantity += amount;
    }

    /**
     * 상품 태그 중복 체크
     * @param requestProductTags
     */
    private void checkDuplicateProductTags(List<ProductTag> requestProductTags) {
        Set<ProductTag> distinctTags = new HashSet<>(requestProductTags);
        if (distinctTags.size() != requestProductTags.size()) {
            throw new DuplicateProductTagsException(requestProductTags);
        }
    }

}