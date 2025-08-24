package com.example.study.product.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.product.command.application.ProductDto;
import com.example.study.product.command.application.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ApiSuccessResponse<Void> save(@Valid @RequestBody ProductDto dto){
        productService.saveProduct(dto);
        return ApiSuccessResponse.empty();
    }

    @PutMapping("/{productId}")
    public ApiSuccessResponse<Void> update(@PathVariable Long productId, @Valid @RequestBody ProductDto dto){
        productService.updateProduct(productId, dto);
        return ApiSuccessResponse.empty();
    }

    @DeleteMapping("/{productId}")
    public ApiSuccessResponse<Void> delete(@PathVariable Long productId){
        productService.deleteProduct(productId);
        return ApiSuccessResponse.empty();
    }

}
