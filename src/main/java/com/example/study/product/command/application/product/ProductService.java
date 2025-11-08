package com.example.study.product.command.application.product;

import com.example.study.product.command.domain.product.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 상품 상세 조회
     * @param productId
     * @return
     */
    @Transactional
    public ProductResponseDto getProduct(Long productId){
        Product product = productRepository.findProductById(productId).orElseThrow(ProductNotFoundException::new);

        if(product instanceof DeliveryProduct){
            return ProductResponseDto.deliveryProduct(product, ProductType.DELIVERY.name());
        } else if(product instanceof CouponProduct){
            return ProductResponseDto.couponProduct(product, ProductType.COUPON.name());
        } else {
            throw new ProductNotFoundException();
        }
    }

    /**
     * 배송형 상품 저장
     * @param dto
     * @return 저장된 배송형 상품 Id
     */
    @Transactional
    public Long saveDeliveryProduct(ProductRequestDto dto){

        DeliveryProduct delivertProduct = new DeliveryProduct(
                dto.name()
                , dto.price()
                , dto.stockQuantity()
                , dto.description()
                , dto.productStatus()
                , dto.deliveryProduct().fee()
                , dto.deliveryProduct().weight()
        );

        List<ProductTag> requestTag = dto.productTags().stream()
                .map(i -> new ProductTag(i.tagName()))
                .toList();
        delivertProduct.assignProductTags(requestTag);

        return productRepository.save(delivertProduct).getId();
    }

    /**
     * 배송형 상품 수정
     * @param productId
     * @param dto
     */
    @Transactional
    public void updateDeliveryProduct(Long productId, ProductRequestDto dto){
        DeliveryProduct deliveryProduct = productRepository.findDeliveryProductById(productId).orElseThrow(ProductNotFoundException::new);

        deliveryProduct.update(
                dto.name()
                , dto.price()
                , dto.stockQuantity()
                , dto.description()
                , dto.productStatus());

        List<ProductTag> newTags = dto.productTags().stream()
                .map(productTagDto -> new ProductTag(productTagDto.tagName()))
                .toList();
        deliveryProduct.updateProductTags(deliveryProduct, newTags);

        deliveryProduct.assignFee(dto.deliveryProduct().fee());
        deliveryProduct.assignWeight(dto.deliveryProduct().weight());
    }

    /**
     * 쿠폰형 상품 저장
     * @param dto
     * @return 저장된 쿠폰형 상품 Id
     */
    @Transactional
    public Long saveCouponProduct(ProductRequestDto dto){

        CouponProduct couponProduct = new CouponProduct(
                dto.name()
                , dto.price()
                , dto.stockQuantity()
                , dto.description()
                , dto.productStatus()
                , dto.couponProduct().discountPrice()
                , dto.couponProduct().effectiveDay()
        );

        List<ProductTag> requestTag = dto.productTags().stream()
                .map(i -> new ProductTag(i.tagName()))
                .toList();
        couponProduct.assignProductTags(requestTag);

        return productRepository.save(couponProduct).getId();
    }

    /**
     * 쿠폰형 상품 수정
     * @param productId
     * @param dto
     */
    @Transactional
    public void updateCouponProduct(Long productId, ProductRequestDto dto){
        CouponProduct couponProduct = productRepository.findCouponProductById(productId).orElseThrow(ProductNotFoundException::new);

        couponProduct.update(
                dto.name()
                , dto.price()
                , dto.stockQuantity()
                , dto.description()
                , dto.productStatus());

        List<ProductTag> newTags = dto.productTags().stream()
                .map(productTagDto -> new ProductTag(productTagDto.tagName()))
                .toList();
        couponProduct.updateProductTags(couponProduct, newTags);

        couponProduct.assignDiscountPrice(dto.couponProduct().discountPrice());
        couponProduct.assignEffectiveDay(dto.couponProduct().effectiveDay());
    }

    /**
     * 상품 삭제
     * @param productId
     */
    @Transactional
    public void deleteProduct(Long productId){
        Product product = productRepository.findProductById(productId).orElseThrow(ProductNotFoundException::new);
        productRepository.delete(product);
    }
}
