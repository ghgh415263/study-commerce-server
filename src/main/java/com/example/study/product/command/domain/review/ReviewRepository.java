package com.example.study.product.command.domain.review;

import java.util.Optional;

public interface ReviewRepository {
    Review save(Review review);

    Optional<Review> findByReviewId(Long reviewId);

    void delete(Review review);
}
