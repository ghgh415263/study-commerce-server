package com.example.study.product.command.infra;

import com.example.study.product.command.domain.review.Review;
import com.example.study.product.query.dao.ReviewQueryDto;
import com.example.study.product.command.domain.review.ReviewRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JpaReviewRepository implements ReviewRepository {

    private final EntityManager entityManager;

    @Override
    public Review save(Review review){
        if (review.getId() == null) {
            entityManager.persist(review);
            return review;
        } else {
            return entityManager.merge(review);
        }
    }

    @Override
    public Optional<Review> findByReviewId(Long ReviewId) {
        return Optional.ofNullable(entityManager.find(Review.class, ReviewId));
    }

    @Override
    public void delete(Review review){
        entityManager.remove(review);
    }
}