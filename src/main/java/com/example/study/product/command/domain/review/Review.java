package com.example.study.product.command.domain.review;

import com.example.study.common.persistance.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.util.UUID;

@Getter
@Entity
@Audited
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "member_id", nullable = false)
    private UUID memberId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int star;

    public Review(UUID memberId, Long productId, String content, int star){
        this.memberId = memberId;
        this.productId = productId;
        this.content = content;
        this.star = star;
    }

    public void update(String content, int star){
        this.content = content;
        this.star = star;
    }
}
