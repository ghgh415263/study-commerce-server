package com.example.study.product.query.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ReviewQueryDto {
    private Long id;          // review id
    private String loginId;
    private UUID memberId;  // 회원 PK (Review.memberId)
    private String content;
    private int star;
}