package com.example.study.common.authentication.fo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklist, Long> {

    Optional<JwtBlacklist> findByJwtHashId(String jwtHashId);

    List<JwtBlacklist> findByExpiredAtBefore(LocalDateTime now);
}
