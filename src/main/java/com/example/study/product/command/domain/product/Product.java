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
    private Set<ProductTag> productTags = new HashSet<>();

    // 읽기 전용
    @Column(name = "product_type", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private ProductType productType;

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

    public void assignProductTags(List<ProductTag> tags) {
        checkDuplicateProductTags(tags);

        List<ProductTag> duplicates = tags.stream()
                .filter(pt -> this.productTags.contains(pt))
                .toList();

        if (!duplicates.isEmpty()) {
            throw new DuplicateProductTagsException(duplicates);
        }

        tags.forEach(pt -> {
            this.productTags.add(pt);
            pt.assignProduct(this);
        });
    }

    public void updateProductTags(Product product, List<ProductTag> newTags) {
        product.getProductTags().clear();
        assignProductTags(newTags);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id != null && id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
