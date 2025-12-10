package com.example.study.common.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileStoreClient {

    /**
     * 파일 저장
     * @param file
     * @return 저장된 파일명 (서버 기준)
     */
    String store(MultipartFile file);

    /**
     * 파일 삭제
     * @param storedFileName
     */
    void delete(String storedFileName);
}