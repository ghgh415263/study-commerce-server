package com.example.study.product.command.domain.review;

import java.util.List;

public interface ReviewImageBulkRepository {
    void saveAll(List<ReviewImage> ReviewImages);
}
