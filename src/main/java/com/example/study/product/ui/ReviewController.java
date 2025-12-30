package com.example.study.product.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.fo.Authentication;
import com.example.study.product.command.application.review.ReviewImageService;
import com.example.study.product.command.application.review.ReviewCreateRequestDto;
import com.example.study.product.command.application.review.ReviewService;
import com.example.study.product.command.application.review.ReviewUpdateRequestDto;
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
    private final ReviewImageService reviewImageService;

    @PostMapping(value = "/reviews")
    @Operation(summary = "리뷰 등록", description = "리뷰를 등록한다")
    public ApiSuccessResponse<Void> saveReview(@Valid @RequestBody ReviewCreateRequestDto dto, Authentication authentication){

        reviewService.saveReview(authentication.getMemberId(), dto);

        return ApiSuccessResponse.empty();
    }

    @PutMapping(value = "/reviews/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "리뷰를 수정한다")
    public ApiSuccessResponse<Void> updateReview(@PathVariable Long reviewId,
                                                 @Valid @RequestBody ReviewUpdateRequestDto dto,
                                                 Authentication authentication){

        reviewService.updateReview(authentication.getMemberId(), reviewId, dto);

        return ApiSuccessResponse.empty();
    }

    @DeleteMapping("/reviews/{reviewId}")
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제한다")
    public ApiSuccessResponse<Void> deleteReview(@PathVariable Long reviewId, Authentication authentication){

        reviewService.deleteReview(authentication.getMemberId(), reviewId);

        return ApiSuccessResponse.empty();
    }

    /**
     * 리뷰 이미지 등록 순서
     *  1. 리뷰 이미지 파일 업로드 api 호출
     *  2. 1번 결과값을 리뷰 등록 api에 넣어 등록
     */
    @PostMapping(value = "/reviews/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "리뷰 이미지 파일 업로드", description = "리뷰 이미지 파일을 업로드한다.")
    public ApiSuccessResponse<List<String>> uploadReviewImageFile(@Valid @RequestPart(required = false) List<MultipartFile> images){
        return ApiSuccessResponse.of(reviewImageService.uploadReviewImageFile(images));
    }
}