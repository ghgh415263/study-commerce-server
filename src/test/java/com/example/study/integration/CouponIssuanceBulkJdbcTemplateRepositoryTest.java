package com.example.study.integration;

import com.example.study.order.command.domain.CouponIssuance;
import com.example.study.order.command.infra.CouponIssuanceBulkJdbcTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestPersistenceAuditorConfig.class)
@DataJpaTest
public class CouponIssuanceBulkJdbcTemplateRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AuditorAware<String> auditorAware;

    private CouponIssuanceBulkJdbcTemplateRepository repository;

    @BeforeEach
    void setUp() {
        repository = new CouponIssuanceBulkJdbcTemplateRepository(jdbcTemplate, auditorAware);
    }

    @Test
    void insertTest() {
        // given
        List<CouponIssuance> entities = List.of(
                new CouponIssuance(101L,"010-1111-1111"),
                new CouponIssuance(102L, "010-1111-1111"),
                new CouponIssuance(103L, "010-1111-1111")
        );

        // when
        repository.saveAll(entities);

        // then
        List<Long> ids = jdbcTemplate.queryForList("SELECT id FROM coupon_issuance", Long.class);
        assertThat(ids).hasSize(3);
    }
}
