package com.example.study.product.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.CustomPage;
import com.example.study.product.query.dao.ReviewDao;
import com.example.study.product.query.dao.ReviewQueryDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReviewPagingController {

    private final ReviewDao reviewDao;

    @GetMapping("/products/{productId}/reviews")
    public ApiSuccessResponse<CustomPage<ReviewQueryDto>> getProductReviews(@PathVariable Long productId,
                                                                            @RequestParam(defaultValue = "1") @Min(1) int page,
                                                                            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size){
        return ApiSuccessResponse.of(reviewDao.findByProductIdWithPaging(productId, page, size));
    }

}
