package com.example.study.product.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.fo.Authentication;
import com.example.study.product.command.application.review.ReviewRequestDto;
import com.example.study.product.command.application.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "FO Review API", description = "FO 리뷰")
@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping(value = "/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "리뷰 등록", description = "리뷰를 등록한다")
    public ApiSuccessResponse<Void> saveReview(@Valid @RequestPart("dto") ReviewRequestDto dto,
                                               @RequestPart(required = false) List<MultipartFile> images,
                                               Authentication authentication){

        reviewService.saveReview(authentication.getMemberId(), dto, images);

        return ApiSuccessResponse.empty();
    }

    @PutMapping(value = "/reviews/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "리뷰 수정", description = "리뷰를 수정한다")
    public ApiSuccessResponse<Void> updateReview(@PathVariable Long reviewId,
                                                 @Valid @RequestPart("dto") ReviewRequestDto dto,
                                                 @RequestParam(value = "deleteImageIds", required = false) List<Long> deleteImageIds,
                                                 @RequestPart(value="newImages", required=false) List<MultipartFile> newImages,
                                                 Authentication authentication){

        reviewService.updateReview(authentication.getMemberId(), reviewId, dto, deleteImageIds, newImages);

        return ApiSuccessResponse.empty();
    }

    @DeleteMapping("/reviews/{reviewId}")
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제한다")
    public ApiSuccessResponse<Void> deleteReview(@PathVariable Long reviewId,
                                                 Authentication authentication){

        reviewService.deleteReview(authentication.getMemberId(), reviewId);

        return ApiSuccessResponse.empty();
    }
}