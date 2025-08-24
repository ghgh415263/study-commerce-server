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

    @Transactional
    public Long saveProduct(ProductDto dto){

        /* 상품 정보 등록 */
        Product product = new Product(dto.name(), dto.price(), dto.stockQuantity(), dto.description());
        product.setProductStatus(dto.productStatus());
        for(ProductTagDto productTag : dto.productTags()){
            product.setProductTags(new ProductTag(productTag.tagName()));
        }

        /* 상품 타입별 정보 등록 */
        ProductType productType = ProductType.from(dto.productType());
        if(ProductType.DELIVERY == productType){
            DeliveryProduct delivertProduct = DeliveryProduct.fromProduct(product, dto.deliveryProduct().fee(), dto.deliveryProduct().weight());
            return productRepository.save(delivertProduct).getId();
        } else if(ProductType.COUPON == productType){
            CouponProduct couponProduct = CouponProduct.fromProduct(product, dto.couponProduct().discountPrice(), dto.couponProduct().effectiveDay());
            return productRepository.save(couponProduct).getId();
        } else { /* ProductType.PRODUCT */
            return productRepository.save(product).getId();
        }
    }

    @Transactional
    public void updateProduct(Long productId, ProductDto dto){
        /* 상품 정보 조회 및 수정*/
//        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
//        product.update(dto.name(), dto.price(), dto.stockQuantity(), dto.description(), dto.productStatus());
//        for(ProductTagDto productTag : dto.productTags()){
//            product.setProductTags(new ProductTag(productTag.tagName()));
//        }

        /* 상품 타입별 정보 조회 후 처리 */
//        if (product instanceof DeliveryProduct deliveryProduct) {
//            deliveryProduct.setFee(dto.deliveryProduct().fee());
//            deliveryProduct.setWeight(dto.deliveryProduct().weight());
//        } else if (product instanceof CouponProduct couponProduct) {
//            couponProduct.setDiscountPrice(dto.couponProduct().discountPrice());
//            couponProduct.setEffectiveDay(dto.couponProduct().effectiveDay());
//        }

        /* 상품 타입별 정보 조회 후 처리 */
        ProductType productType = ProductType.from(dto.productType());
        if(ProductType.DELIVERY == productType){
            DeliveryProduct deliveryProduct = productRepository.findByDeliveryProductId(productId).orElseThrow(ProductNotFoundException::new);
            deliveryProduct.update(dto.name(), dto.price(), dto.stockQuantity(), dto.description(), dto.productStatus());
            List<ProductTag> newTags = dto.productTags().stream()
                    .map(productTagDto -> new ProductTag(productTagDto.tagName()))
                    .toList();
            deliveryProduct.updateProductTags(deliveryProduct, newTags);

            deliveryProduct.setFee(dto.deliveryProduct().fee());
            deliveryProduct.setWeight(dto.deliveryProduct().weight());

        } else if(ProductType.COUPON == productType){
            CouponProduct couponProduct = productRepository.findByCouponProductId(productId).orElseThrow(ProductNotFoundException::new);
            couponProduct.update(dto.name(), dto.price(), dto.stockQuantity(), dto.description(), dto.productStatus());
            List<ProductTag> newTags = dto.productTags().stream()
                    .map(productTagDto -> new ProductTag(productTagDto.tagName()))
                    .toList();
            couponProduct.updateProductTags(couponProduct, newTags);

            couponProduct.setDiscountPrice(dto.couponProduct().discountPrice());
            couponProduct.setEffectiveDay(dto.couponProduct().effectiveDay());

        } else if(ProductType.PRODUCT == productType){
            Product product = productRepository.findByProductId(productId).orElseThrow(ProductNotFoundException::new);
            product.update(dto.name(), dto.price(), dto.stockQuantity(), dto.description(), dto.productStatus());
            List<ProductTag> newTags = dto.productTags().stream()
                    .map(productTagDto -> new ProductTag(productTagDto.tagName()))
                    .toList();
            product.updateProductTags(product, newTags);
        }

    }

    @Transactional
    public void deleteProduct(Long productId){
        Product product = productRepository.findByProductId(productId).orElseThrow(ProductNotFoundException::new);
        productRepository.delete(product);
    }
}
