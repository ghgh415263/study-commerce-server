package com.example.study.review.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.Authentication;
import com.example.study.common.authentication.AuthenticationConstant;
import com.example.study.review.command.application.ReviewRequestDto;
import com.example.study.review.command.application.ReviewResponseDto;
import com.example.study.review.command.application.ReviewService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/products/{productId}/reviews/{page}")
    public ApiSuccessResponse<List<ReviewResponseDto>> getProductReviews(@PathVariable Long productId, @PathVariable int page){
        List<ReviewResponseDto> review = reviewService.getProductReviews(productId, page);
        return ApiSuccessResponse.of(review);
    }

    @PostMapping("/reviews")
    public ApiSuccessResponse<Void> saveReview(@Valid @RequestBody ReviewRequestDto dto,
                                               HttpSession session){
        Authentication authentication = (Authentication) session.getAttribute(AuthenticationConstant.AUTHENTICATION);
        reviewService.saveReview(authentication.getMemberId(), dto);
        return ApiSuccessResponse.empty();
    }

    @PutMapping("/reviews/{reviewId}")
    public ApiSuccessResponse<Void> updateReview(@PathVariable Long reviewId,
                                                 @Valid @RequestBody ReviewRequestDto dto,
                                                 HttpSession session){
        Authentication authentication = (Authentication) session.getAttribute(AuthenticationConstant.AUTHENTICATION);
        reviewService.updateReview(authentication.getMemberId(), reviewId, dto);
        return ApiSuccessResponse.empty();
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ApiSuccessResponse<Void> deleteReview(@PathVariable Long reviewId,
                                                 HttpSession session){
        Authentication authentication = (Authentication) session.getAttribute(AuthenticationConstant.AUTHENTICATION);
        reviewService.deleteReview(authentication.getMemberId(), reviewId);
        return ApiSuccessResponse.empty();
    }


}
