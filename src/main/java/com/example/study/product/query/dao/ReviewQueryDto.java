package com.example.study.product.query.dao;

import com.example.study.common.util.MaskingUtils;
import lombok.Getter;

@Getter
public class ReviewQueryDto {
    private Long id;          // review id
    private String loginId;
    private Long memberId;  // 회원 PK (Review.memberId)
    private String content;
    private int star;

    public ReviewQueryDto (Long id, String loginId, Long memberId, String content, int star) {
        this.id = id;
        this.loginId = MaskingUtils.maskId(loginId);
        this.memberId = memberId;
        this.content = content;
        this.star = star;
    }
}