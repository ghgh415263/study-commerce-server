package com.example.study.product.command.application.review;

import com.example.study.common.file.FileStoreClient;
import com.example.study.product.command.domain.review.Review;
import com.example.study.product.command.domain.review.ReviewImage;
import com.example.study.product.command.domain.review.ReviewImageBulkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class ReviewImageService {

    private final FileStoreClient fileStoreClient;
    private final ReviewImageBulkRepository reviewImageBulkRepository;

    /**
     * 리뷰 이미지 파일 저장 & DB 저장
     * @param review
     * @param images
     */
    public void saveReviewImages(Review review, List<MultipartFile> images) {

        List<ReviewImage> reviewImages = new ArrayList<>(); // ReviewImage bulk insert list

        for (MultipartFile image : images) { // 이미지 파일 저장
            if (image.isEmpty()) continue;

            if (!Objects.requireNonNull(image.getContentType()).startsWith("image/")) { // Type validation
                throw new InvalidImageFileException("파일은 이미지 파일만 업로드 가능합니다.");
            }

            String storedFileName = fileStoreClient.store(image);

            ReviewImage reviewImage = new ReviewImage(
                    review,
                    image.getOriginalFilename(),
                    storedFileName
            );

            reviewImages.add(reviewImage);
        }

        if (!reviewImages.isEmpty()) { // Save bulk reviewImage list
            reviewImageBulkRepository.saveAll(reviewImages);
        }
    }

    /**
     * 리뷰 이미지 수정(기존 이미지를 삭제한다)
     * @param review
     * @param deleteImageIds
     */
    public void deleteReviewImages(Review review, List<Long> deleteImageIds) {
        review.getImages().removeIf(img -> deleteImageIds.contains(img.getId()));
    }
}
