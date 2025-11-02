package com.example.study.product.command.domain.product;

import com.example.study.common.persistance.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;

import java.util.Objects;

@ToString(exclude = {"product"})
@Entity
@Getter
@Audited
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product_tag", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "tagName"})
})
public class ProductTag extends BaseUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_tag_id")
    private Long id;

    @Column(nullable = false)
    private String tagName;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductTag(String tagName){
        this.tagName = tagName;
    }

    public void assignProduct(Product product){
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductTag that = (ProductTag) o;
        return Objects.equals(tagName, that.tagName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagName);
    }
}
