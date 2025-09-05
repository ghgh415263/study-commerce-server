package com.example.study.review.command.domain;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    Review save(Review review);

    Optional<Review> findByReviewId(Long reviewId);

    List<Review> findByProductIdWithPaging(Long productId, int page);

    void delete(Review review);
}
