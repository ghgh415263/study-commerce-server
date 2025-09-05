package com.example.study.review.command.infra;

import com.example.study.member.command.domain.MemberStatus;
import com.example.study.review.command.application.ReviewConstant;
import com.example.study.review.command.domain.Review;
import com.example.study.review.command.domain.ReviewRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JpaReviewRepository implements ReviewRepository {

    private final EntityManager entityManager;

    private final JdbcTemplate jdbcTemplate;

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
    public List<Review> findByProductIdWithPaging(Long productId, int page){

        int offset = page * ReviewConstant.pagingSize;

        String sql = """
            SELECT r.review_id, r.product_id, r.login_id, r.content, r.star, m.status AS member_status
            FROM review r
            LEFT JOIN member m ON r.login_id = m.login_id
            WHERE m.status != ?
                AND r.product_id = ?
            ORDER BY r.created_at DESC
            LIMIT ? OFFSET ?
        """;

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    Review review = new Review(rs.getString("login_id"),
                            rs.getLong("product_id"),
                            rs.getString("content"),
                            rs.getInt("star"));
                    review.assignMemberStatus(rs.getString("member_status"));
                    return review;
                },
                MemberStatus.SUSPENDED.name(),
                productId,
                ReviewConstant.pagingSize,
                offset);
    }

    @Override
    public void delete(Review review){
        entityManager.remove(review);
    }
}