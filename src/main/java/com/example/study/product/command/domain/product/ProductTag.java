package com.example.study.product.command.domain.product;

import com.example.study.common.persistance.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

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
}
