package com.example.study.product.command.domain.review;

import com.example.study.common.authentication.fo.UnauthenticatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewImageBulkRepositoryImpl implements ReviewImageBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    private final AuditorAware<String> auditorAware;

    public void saveAll(List<ReviewImage> reviewImages) {
        if (reviewImages == null || reviewImages.isEmpty()) return;

        String currentAuditor = auditorAware.getCurrentAuditor()
                .orElseThrow(UnauthenticatedException::new);

        String sql = """
            INSERT INTO review_image (
                created_by, modified_by, stored_file_name, review_id
            )
            VALUES (?, ?, ?, ?)
            """;

        jdbcTemplate.batchUpdate(sql, reviewImages, reviewImages.size(), (ps, reviewImage) -> {
            ps.setString(1, currentAuditor); // created_by
            ps.setString(2, currentAuditor); // modified_by
            ps.setString(3, reviewImage.getStoredFileName());
            ps.setObject(4, reviewImage.getReview().getId());
        });
    }
}
