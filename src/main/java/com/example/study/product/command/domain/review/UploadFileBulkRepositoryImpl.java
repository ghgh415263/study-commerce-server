package com.example.study.product.command.domain.review;

import com.example.study.common.authentication.fo.UnauthenticatedException;
import com.example.study.common.util.UUIDUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UploadFileBulkRepositoryImpl implements UploadFileBulkRepository {

    private final JdbcTemplate jdbcTemplate;
    private final AuditorAware<String> auditorAware;

    public void saveAll(List<UploadFile> uploadFiles) {
        if (uploadFiles == null || uploadFiles.isEmpty()) return;

        String currentAuditor = auditorAware.getCurrentAuditor()
                .orElseThrow(UnauthenticatedException::new);

        String sql = """
            INSERT INTO upload_file (
                created_by, stored_file_name, original_file_name, status, expired_at, id
            )
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        jdbcTemplate.batchUpdate(sql, uploadFiles, uploadFiles.size(), (ps, uploadFile) -> {
            ps.setString(1, currentAuditor); // created_by
            ps.setString(2, uploadFile.getStoredFileName());
            ps.setString(3, uploadFile.getOriginalFileName());
            ps.setString(4, uploadFile.getStatus().name());
            ps.setObject(5, uploadFile.getExpiredAt());
            ps.setObject(6, UUIDUtils.toBytes(uploadFile.getId()));
        });
    }
}
