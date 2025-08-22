package com.example.study.member.command.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MasterAccountInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String loginId = "master";
        String rawPassword = "masterPassword123!";
        String encodedPassword = passwordEncoder.hashPassword(rawPassword);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM backoffice_member WHERE login_id = ?",
                Integer.class,
                loginId
        );

        if (count != null && count == 0) {
            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            jdbcTemplate.update(
                    "INSERT INTO backoffice_member " +
                            "(id, login_id, password, created_by, created_at, modified_by, modified_at) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    asBytes(id), loginId, encodedPassword,
                    "system", now,
                    "system", now
            );
        }
    }

    private byte[] asBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }
}