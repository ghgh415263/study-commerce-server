package com.example.study.product.query.dao;

import com.example.study.common.CustomPage;

public interface ProductDao {
    CustomPage<ProductQueryDto> findByProductIdWithPaging(String type, int page, int size);
}
