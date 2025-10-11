package com.example.study.product.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.CustomPage;
import com.example.study.product.query.dao.BOProductQueryDto;
import com.example.study.product.query.dao.ProductDao;
import com.example.study.product.query.dao.FOProductQueryDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ProductPagingController {

    private final ProductDao productDao;

    /* FO 상품 목록 */
    @GetMapping("/products")
    public ApiSuccessResponse<CustomPage<FOProductQueryDto>> getFOProductList(@RequestParam(defaultValue = "1") @Min(1) int page,
                                                                            @RequestParam(defaultValue = "10") @Min(1) @Max(500) int size){
        return ApiSuccessResponse.of(productDao.findByFOProductIdWithPaging(page, size));
    }

    /* BO 상품 목록 */
    @GetMapping("/backoffice/products")
    public ApiSuccessResponse<CustomPage<BOProductQueryDto>> getBOProductList(@RequestParam(defaultValue = "1") @Min(1) int page,
                                                                              @RequestParam(defaultValue = "10") @Min(1) @Max(500) int size){
        return ApiSuccessResponse.of(productDao.findByBOProductIdWithPaging(page, size));
    }
}
