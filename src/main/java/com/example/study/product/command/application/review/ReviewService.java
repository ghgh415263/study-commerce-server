package com.example.study.product.command.application.review;

import com.example.study.common.file.FileStoreClient;
import com.example.study.product.command.application.product.ProductNotFoundException;
import com.example.study.product.command.domain.product.Product;
import com.example.study.product.command.domain.product.ProductRepository;
import com.example.study.product.command.domain.review.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ProductRepository productRepository;
    private final FileStoreClient fileStoreClient;
    private final ReviewImageService reviewImageService;

    /**
     * 리뷰 저장
     * @param memberId
     * @param dto
     * @return
     */
    @Transactional
    public Long saveReview(Long memberId, ReviewCreateRequestDto dto){

        // 1) 텍스트 저장
        Product product = productRepository.findById(dto.productId()).orElseThrow(ProductNotFoundException::new);
        Review review = new Review(memberId, product.getId(), dto.content(), dto.star());
        Long saveReviewId = reviewRepository.save(review).getId();

        // 2) 이미지 저장
        List<String> uploadReviewImageFileNames = dto.uploadReviewImageFileNames();
        if(uploadReviewImageFileNames != null && !uploadReviewImageFileNames.isEmpty()) {
            reviewImageService.saveReviewImages(review, uploadReviewImageFileNames);
        }

        return saveReviewId;
    }

    /**
     * 리뷰 수정
     * @param memberId
     * @param reviewId
     * @param dto
     * @return
     */
    @Transactional
    public Long updateReview(Long memberId, Long reviewId, ReviewUpdateRequestDto dto){

        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);

        if (!review.getMemberId().equals(memberId)){
            throw new InvalidReviewAuthenticationException();
        }

        // 1) 텍스트 수정
        review.update(dto.content(), dto.star());

        // 2) 이미지 삭제
        if (dto.deleteImageIds() != null && !dto.deleteImageIds().isEmpty()) {
            List<ReviewImage> imagesToDelete = reviewImageRepository.findAllById(dto.deleteImageIds());

            reviewImageService.deleteReviewImages(review, dto.deleteImageIds()); // DB 삭제

            for (ReviewImage image : imagesToDelete) {
                fileStoreClient.delete(image.getStoredFileName()); // 서버 파일 삭제
            }
        }

        // 3) 새 이미지 추가
        if (dto.uploadReviewImageFileNames() != null && !dto.uploadReviewImageFileNames().isEmpty()) {
            reviewImageService.saveReviewImages(review, dto.uploadReviewImageFileNames());
        }

        return review.getId();
    }

    /**
     * 리뷰 삭제
     * @param reviewId
     */
    @Transactional
    public void deleteReview(Long memberId, Long reviewId){
        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);
        if (!review.getMemberId().equals(memberId)){
            throw new InvalidReviewAuthenticationException();
        }
        
        // 텍스트 삭제
        reviewRepository.delete(review);

        // 이미지 파일 삭제
        for (ReviewImage image : review.getImages()) {
            fileStoreClient.delete(image.getStoredFileName());
        }
    }
}
