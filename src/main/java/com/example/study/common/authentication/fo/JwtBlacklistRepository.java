package com.example.study.common.authentication.fo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklist, Long> {

    Optional<JwtBlacklist> findByJwtHashId(String jwtHashId);

    @Modifying
    @Query(value = "DELETE FROM jwt_blacklist WHERE expired_at < :now LIMIT :batchSize",
            nativeQuery = true)
    int deleteExpired(@Param("now") Instant now, @Param("batchSize") int batchSize);
}
