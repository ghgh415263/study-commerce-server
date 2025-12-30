package com.example.study.product.command.domain.review;

import com.example.study.common.file.FileStoreClient;
import com.example.study.product.command.application.review.FileStoreException;
import com.example.study.product.command.application.review.InvalidImageFileException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class LocalFileStoreClientReview implements FileStoreClient {
    @Value("${file.review-image-dir}")
    private String reviewImageDir;

    /**
     * 리뷰 이미지 저장 폴더 생성
     */
    @PostConstruct
    public void init() {
        File dir = new File(reviewImageDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new IllegalStateException("리뷰 이미지 디렉토리 생성 실패: " + reviewImageDir);
            }
        }
    }

    /**
     * 리뷰 이미지 저장
     * @param file
     * @return
     */
    public String store( UUID uploadFileId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일입니다.");
        }

        String originalFilename = file.getOriginalFilename();
        String storedFileName = createStoredFileName(originalFilename,uploadFileId); // UUID + . + 확장자

        try {
            file.transferTo(new File(getFullPath(storedFileName)));
        } catch (IOException e) {
            throw new FileStoreException("파일 저장 실패", e);
        }

        return storedFileName;
    }

    private String getFullPath(String fileName) {
        return reviewImageDir + fileName;
    }

    private String createStoredFileName(String originalFilename, UUID uploadFileId) {
        String ext = extractExt(originalFilename);
        return uploadFileId.toString() + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        if (pos == -1) {
            throw new InvalidImageFileException("확장자가 없는 파일입니다.");
        }
        return originalFilename.substring(pos + 1);
    }

    /**
     * 리뷰 이미지 파일 삭제
     * @param storedFileName
     */
    public void delete(String storedFileName) {
        Path path = Paths.get(reviewImageDir, storedFileName);

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new FileStoreException("파일 삭제 실패", e);
        }
    }
}
