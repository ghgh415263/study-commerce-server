package com.example.study.product.query.dao;

import com.example.study.common.CustomPage;

public interface ProductDao {
    CustomPage<FOProductQueryDto> findByFOProductIdWithPaging(int page, int size);

    CustomPage<BOProductQueryDto> findByBOProductIdWithPaging(int page, int size);
}
