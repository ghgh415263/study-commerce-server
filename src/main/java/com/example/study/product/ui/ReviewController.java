package com.example.study.product.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.fo.Authentication;
import com.example.study.product.command.application.review.ReviewRequestDto;
import com.example.study.product.command.application.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public ApiSuccessResponse<Void> saveReview(@Valid @RequestBody ReviewRequestDto dto,
                                               Authentication authentication){
        reviewService.saveReview(authentication.getMemberId(), dto);
        return ApiSuccessResponse.empty();
    }

    @PutMapping("/reviews/{reviewId}")
    public ApiSuccessResponse<Void> updateReview(@PathVariable Long reviewId,
                                                 @Valid @RequestBody ReviewRequestDto dto,
                                                 Authentication authentication){
        reviewService.updateReview(authentication.getMemberId(), reviewId, dto);
        return ApiSuccessResponse.empty();
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ApiSuccessResponse<Void> deleteReview(@PathVariable Long reviewId,
                                                 Authentication authentication){
        reviewService.deleteReview(authentication.getMemberId(), reviewId);
        return ApiSuccessResponse.empty();
    }


}
