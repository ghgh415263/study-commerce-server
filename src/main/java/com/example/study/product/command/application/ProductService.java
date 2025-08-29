package com.example.study.product.command.application;

import com.example.study.product.command.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        Product product = productRepository.findByProductId(productId).orElseThrow(ProductNotFoundException::new);

        if(product instanceof DeliveryProduct){
            return ProductResponseDto.deliveryProduct(product, ProductType.DELIVERY.name());
        } else if(product instanceof CouponProduct){
            return ProductResponseDto.couponProduct(product, ProductType.COUPON.name());
        } else {
            throw new ProductNotFoundException();
        }
    }


    /**
     * 상품 기본 정보 저장
     * @param dto
     * @return 저장된 상품 엔티티
     */
    private Product saveProduct(ProductRequestDto dto){
        Product product = new Product(
                dto.name()
                , dto.price()
                , dto.stockQuantity()
                , dto.description());

        product.setProductStatus(dto.productStatus());

        for(ProductTagDto productTag : dto.productTags()){
            product.setProductTags(new ProductTag(productTag.tagName()));
        }
        return product;
    }

    /**
     * 배송형 상품 저장
     * @param dto
     * @return 저장된 배송형 상품 Id
     */
    @Transactional
    public Long saveDeliveryProduct(ProductRequestDto dto){
        DeliveryProduct delivertProduct = DeliveryProduct.fromProduct(
                saveProduct(dto)
                , dto.deliveryProduct().fee()
                , dto.deliveryProduct().weight());
        return productRepository.save(delivertProduct).getId();
    }

    /**
     * 배송형 상품 수정
     * @param productId
     * @param dto
     */
    @Transactional
    public void updateDeliveryProduct(Long productId, ProductRequestDto dto){
        DeliveryProduct deliveryProduct = productRepository.findByDeliveryProductId(productId).orElseThrow(ProductNotFoundException::new);

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

        deliveryProduct.setFee(dto.deliveryProduct().fee());
        deliveryProduct.setWeight(dto.deliveryProduct().weight());
    }

    /**
     * 쿠폰형 상품 저장
     * @param dto
     * @return 저장된 쿠폰형 상품 Id
     */
    @Transactional
    public Long saveCouponProduct(ProductRequestDto dto){
        CouponProduct couponProduct = CouponProduct.fromProduct(
                saveProduct(dto)
                , dto.couponProduct().discountPrice()
                , dto.couponProduct().effectiveDay());
        return productRepository.save(couponProduct).getId();
    }

    /**
     * 쿠폰형 상품 수정
     * @param productId
     * @param dto
     */
    @Transactional
    public void updateCouponProduct(Long productId, ProductRequestDto dto){
        CouponProduct couponProduct = productRepository.findByCouponProductId(productId).orElseThrow(ProductNotFoundException::new);

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

        couponProduct.setDiscountPrice(dto.couponProduct().discountPrice());
        couponProduct.setEffectiveDay(dto.couponProduct().effectiveDay());
    }

    /**
     * 상품 삭제
     * @param productId
     */
    @Transactional
    public void deleteProduct(Long productId){
        Product product = productRepository.findByProductId(productId).orElseThrow(ProductNotFoundException::new);
        productRepository.delete(product);
    }
}
