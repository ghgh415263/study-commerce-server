package com.example.study.product.command.domain.review;

import java.util.List;

public interface UploadFileBulkRepository {
    void saveAll(List<UploadFile> UploadFiles);
}
