package com.example.study.product.query.dao;

import com.example.study.common.CustomPage;

public interface ReviewDao {

    CustomPage<ReviewQueryDto> findByProductIdWithPaging(Long productId, int page, int size);
}
