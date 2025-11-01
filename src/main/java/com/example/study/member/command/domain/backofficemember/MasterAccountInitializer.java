package com.example.study.member.command.domain.backofficemember;

import com.example.study.member.command.domain.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor  // 임시 테스트용 initializer (운영에는 나가면 안됨)
public class MasterAccountInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String loginId = "master";
        String rawPassword = "masterPassword123!";
        String encodedPassword = passwordEncoder.hashPassword(rawPassword);
        LocalDateTime now = LocalDateTime.now();

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM member_base mb " +
                        "JOIN backoffice_member bm ON mb.id = bm.id " +
                        "WHERE mb.login_id = ?",
                Integer.class,
                loginId
        );

        if (count != null && count == 0) {
            jdbcTemplate.update(
                    "INSERT INTO member_base (login_id, password, created_by, created_at, modified_by, modified_at, member_type) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)",
                    loginId, encodedPassword,
                    "system", now,
                    "system", now,
                    "BACKOFFICE"
            );

            // 방금 삽입한 ID 가져오기
            Long generatedId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

            jdbcTemplate.update(
                    "INSERT INTO backoffice_member (id, role) VALUES (?, ?)",
                    generatedId, BackofficeRole.ADMIN.name()
            );
        }
    }
}