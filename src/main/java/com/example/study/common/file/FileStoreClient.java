package com.example.study.common.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileStoreClient {

    /**
     * 파일 저장
     * @param fileId
     * @param file
     * @return 저장된 파일명 (서버 기준)
     */
    String store(UUID fileId, MultipartFile file);

    /**
     * 파일 삭제
     * @param storedFileName
     */
    void delete(String storedFileName);
}