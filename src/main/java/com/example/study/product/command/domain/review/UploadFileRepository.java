package com.example.study.product.command.domain.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value =  """
        update upload_file u
        set u.status = 'REVIEW_ATTACHED'
        where u.stored_file_name in (:storedFileNames)
          and u.status = 'TEMP'
    """, nativeQuery = true)
    void markAttachedByStoredFileNames(@Param("storedFileNames") List<String> storedFileNames);

    @Modifying
    @Query(value = """
        DELETE FROM upload_file
        WHERE status = 'TEMP'
            AND expired_at < :now
        LIMIT :batchSize
    """, nativeQuery = true)
    int deleteTempUploadFile(@Param("now") LocalDateTime now, @Param("batchSize") int batchSize);
}
