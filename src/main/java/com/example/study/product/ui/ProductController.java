package com.example.study.product.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.product.command.application.ProductRequestDto;
import com.example.study.product.command.application.ProductResponseDto;
import com.example.study.product.command.application.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/backoffice/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ApiSuccessResponse<ProductResponseDto> getProduct(@PathVariable Long productId){
        ProductResponseDto product = productService.getProduct(productId);
        return ApiSuccessResponse.of(product);
    }

    @PostMapping("/delivery")
    public ApiSuccessResponse<Void> saveDeliveryProduct(@Valid @RequestBody ProductRequestDto dto){
        productService.saveDeliveryProduct(dto);
        return ApiSuccessResponse.empty();
    }

    @PutMapping("/delivery/{productId}")
    public ApiSuccessResponse<Void> updateDeliveryProduct(@PathVariable Long productId, @Valid @RequestBody ProductRequestDto dto){
        productService.updateDeliveryProduct(productId, dto);
        return ApiSuccessResponse.empty();
    }

    @PostMapping("/coupon")
    public ApiSuccessResponse<Void> saveCouponProduct(@Valid @RequestBody ProductRequestDto dto){
        productService.saveCouponProduct(dto);
        return ApiSuccessResponse.empty();
    }

    @PutMapping("/coupon/{productId}")
    public ApiSuccessResponse<Void> updateCouponProduct(@PathVariable Long productId, @Valid @RequestBody ProductRequestDto dto){
        productService.updateCouponProduct(productId, dto);
        return ApiSuccessResponse.empty();
    }

    @DeleteMapping("/{productId}")
    public ApiSuccessResponse<Void> delete(@PathVariable Long productId){
        productService.deleteProduct(productId);
        return ApiSuccessResponse.empty();
    }
}
