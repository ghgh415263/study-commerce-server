package com.example.study.product.query.dao;

import com.example.study.product.command.domain.product.ProductStatus;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProductQueryDto {
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    private String description;
    private String productStatus;
    private List<String> productTags = new ArrayList<>();

    public ProductQueryDto(Long id, String name, int price, int stockQuantity, String description, ProductStatus productStatus) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.productStatus = productStatus.name();
    }

    public void addTag(String tagName) {
        this.productTags.add(tagName);
    }
}