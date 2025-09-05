package com.example.study.review.command.domain;

import com.example.study.common.persistance.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

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

    @Column(name = "login_id", nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int star;

    @Transient
    private String memberStatus;

    public Review(String loginId, Long productId, String content, int star){
        this.loginId = loginId;
        this.productId = productId;
        this.content = content;
        this.star = star;
    }

    public void update(String content, int star){
        this.content = content;
        this.star = star;
    }

    public void assignMemberStatus(String memberStatus) {
        this.memberStatus = memberStatus;
    }
}
