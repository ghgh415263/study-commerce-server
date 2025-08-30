package com.example.study.product.command.application;

import com.example.study.product.command.domain.CouponProduct;
import com.example.study.product.command.domain.DeliveryProduct;
import com.example.study.product.command.domain.Product;
import com.example.study.product.command.domain.ProductTag;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProductResponseDto {
    /* 상품 기본 */
    private String name;
    private int price;
    private int stockQuantity;
    private String description;
    private String productStatus;
    private String productType;
    private List<String> productTags = new ArrayList<>();
    /* 공통 date */
    private String createdAt;
    private String createdBy;
    private String modifiedAt;
    private String modifiedBy;
    /* 쿠폰형 상품 */
    private Integer discountPrice;
    private Integer effectiveDay;
    /* 배송형 상품 */
    private Integer fee;
    private Integer weight;

    /** 배송형 상품 생성자 **/
    public ProductResponseDto(DeliveryProduct deliveryProduct, String productType){
        /* 상품 기본 */
        this.name = deliveryProduct.getName();
        this.price = deliveryProduct.getPrice();
        this.stockQuantity = deliveryProduct.getStockQuantity();
        this.description = deliveryProduct.getDescription();
        this.productStatus = deliveryProduct.getProductStatus().name();
        this.productType = productType;
        for(ProductTag productTag : deliveryProduct.getProductTags()){
            this.productTags.add(productTag.getTagName());
        }
        /* 공통 date */
        this.createdAt = ProductUtils.dateFormat(deliveryProduct.getCreatedAt());
        this.createdBy = deliveryProduct.getCreatedBy();
        this.modifiedAt = ProductUtils.dateFormat(deliveryProduct.getModifiedAt());
        this.modifiedBy = deliveryProduct.getModifiedBy();
        /* 배송형 상품 */
        this.fee = deliveryProduct.getFee();
        this.weight = deliveryProduct.getWeight();
    }

    /** 쿠폰형 상품 생성자 **/
    public ProductResponseDto(CouponProduct couponProduct, String productType){
        /* 상품 기본 */
        this.name = couponProduct.getName();
        this.price = couponProduct.getPrice();
        this.stockQuantity = couponProduct.getStockQuantity();
        this.description = couponProduct.getDescription();
        this.productStatus = couponProduct.getProductStatus().name();
        this.productType = productType;
        for(ProductTag productTag : couponProduct.getProductTags()){
            this.productTags.add(productTag.getTagName());
        }
        /* 공통 date */
        this.createdAt = ProductUtils.dateFormat(couponProduct.getCreatedAt());
        this.createdBy = couponProduct.getCreatedBy();
        this.modifiedAt = ProductUtils.dateFormat(couponProduct.getModifiedAt());
        this.modifiedBy = couponProduct.getModifiedBy();
        /* 쿠폰형 상품 */
        this.discountPrice = couponProduct.getDiscountPrice();
        this.effectiveDay = couponProduct.getEffectiveDay();
    }

    public static ProductResponseDto deliveryProduct(Product product, String productType){
        DeliveryProduct deliveryProduct = (DeliveryProduct) product;
        return new ProductResponseDto(deliveryProduct, productType);
    }

    public static ProductResponseDto couponProduct(Product product, String productType){
        CouponProduct couponProduct = (CouponProduct) product;
        return new ProductResponseDto(couponProduct, productType);
    }

}
