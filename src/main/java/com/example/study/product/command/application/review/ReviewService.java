package com.example.study.product.command.application.review;

import com.example.study.common.file.FileStoreClient;
import com.example.study.product.command.application.product.ProductNotFoundException;
import com.example.study.product.command.domain.product.Product;
import com.example.study.product.command.domain.product.ProductRepository;
import com.example.study.product.command.domain.review.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
     * @param images
     * @return
     */
    @Transactional
    public Long saveReview(Long memberId , ReviewRequestDto dto, List<MultipartFile> images){

        // 1) 텍스트 저장
        Product product = productRepository.findById(dto.productId()).orElseThrow(ProductNotFoundException::new);
        Review review = new Review(memberId, product.getId(), dto.content(), dto.star());
        Long saveReviewId = reviewRepository.save(review).getId();

        // 2) 이미지 저장
        if(images != null && !images.isEmpty()) {
            reviewImageService.saveReviewImages(review, images);
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
    public Long updateReview(Long memberId, Long reviewId, ReviewRequestDto dto, List<Long> deleteImageIds, List<MultipartFile> newImages){

        Review review = reviewRepository.findById(reviewId).orElseThrow(ReviewNotFoundException::new);

        if (!review.getMemberId().equals(memberId)){
            throw new InvalidReviewAuthenticationException();
        }

        // 1) 텍스트 수정
        review.update(dto.content(), dto.star());

        // 2) 이미지 삭제
        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            List<ReviewImage> imagesToDelete = reviewImageRepository.findAllById(deleteImageIds);

            reviewImageService.deleteReviewImages(review, deleteImageIds); // DB 삭제

            for (ReviewImage image : imagesToDelete) {
                fileStoreClient.delete(image.getStoredFileName()); // 서버 파일 삭제
            }
        }

        // 3) 새 이미지 추가
        if (newImages != null && !newImages.isEmpty()) {
            reviewImageService.saveReviewImages(review, newImages);
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

        // DB 삭제
        reviewRepository.delete(review);

        // 리뷰 이미지 파일 삭제
        for (ReviewImage image : review.getImages()) {
            fileStoreClient.delete(image.getStoredFileName());
        }
    }
}
