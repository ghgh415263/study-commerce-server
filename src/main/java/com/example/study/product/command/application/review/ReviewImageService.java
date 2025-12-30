package com.example.study.product.command.application.review;

import com.example.study.common.file.FileStoreClient;
import com.example.study.common.util.DateUtils;
import com.example.study.product.command.domain.review.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReviewImageService {

    private final FileStoreClient fileStoreClient;
    private final ReviewImageBulkRepository reviewImageBulkRepository;
    private final UploadFileBulkRepository uploadFileBulkRepository;
    private final UploadFileRepository uploadFileRepository;

    /**
     * 업로드 된 리뷰 이미지 파일 이름 저장
     * @param review
     * @param uploadReviewImageFileNames
     */
    public void saveReviewImages(Review review, List<String> uploadReviewImageFileNames) {
        List<ReviewImage> reviewImages = new ArrayList<>(); // ReviewImage bulk insert list

        for (String uploadReviewImageFileName : uploadReviewImageFileNames) { // 이미지 파일 저장
            if (uploadReviewImageFileName.isBlank()) continue;

            ReviewImage reviewImage = new ReviewImage(review, uploadReviewImageFileName);

            reviewImages.add(reviewImage);
        }

        if (!reviewImages.isEmpty()) { // Save bulk reviewImage list
            reviewImageBulkRepository.saveAll(reviewImages);
        }

        // uploadFile 상태 업데이트
        uploadFileRepository.markAttachedByStoredFileNames(uploadReviewImageFileNames);
    }

    /**
     * 리뷰 이미지 수정(기존 이미지를 삭제한다)
     * @param review
     * @param deleteImageIds
     */
    public void deleteReviewImages(Review review, List<Long> deleteImageIds) {
        review.getImages().removeIf(img -> deleteImageIds.contains(img.getId()));
    }

    /**
     * 리뷰 이미지 등록을 위한 파일 업로드
     *  ㄴ 임시로 파일 업로드 먼저 후에 저장파일 이름을 리턴
     *      (리뷰 등록시 리뷰와 같이 매핑처리 한다.)
     * @param images
     */
    public List<String> uploadReviewImageFile(List<MultipartFile> images) {

        List<UploadFile> uploadReviewImageFiles = new ArrayList<>();

        for (MultipartFile image : images) {
            if (image.isEmpty()) continue;

            if (!Objects.requireNonNull(image.getContentType()).startsWith("image/")) { // Type validation
                throw new InvalidImageFileException("파일은 이미지 파일만 업로드 가능합니다.");
            }

            // uploadFileId
            UUID uploadFileId = UUID.randomUUID();

            // 파일 저장
            String storedFileName = fileStoreClient.store(uploadFileId, image);

            // 파일 이미지 등록 만료시간 30분 지정
            LocalDateTime expiredTime_now_plusMinute_30 = DateUtils.truncateToSeconds(LocalDateTime.now().plusMinutes(30));

            UploadFile uploadFile = UploadFile.createTemp(
                    uploadFileId
                    , storedFileName
                    , image.getOriginalFilename()
                    , expiredTime_now_plusMinute_30);

            uploadReviewImageFiles.add(uploadFile);
        }

        if (!uploadReviewImageFiles.isEmpty()) { // Save bulk reviewImage list
            uploadFileBulkRepository.saveAll(uploadReviewImageFiles);
        }

        return uploadReviewImageFiles.stream()
                .map(UploadFile::getStoredFileName)
                .collect(Collectors.toList());
    }
}