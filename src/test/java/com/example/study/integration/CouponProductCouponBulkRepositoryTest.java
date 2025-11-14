package com.example.study.integration;

import com.example.study.order.couponproductcoupon.command.CouponProductCoupon;
import com.example.study.order.couponproductcoupon.command.CouponProductCouponBulkRepository;
import com.example.study.order.couponproductcoupon.command.CouponProductCouponBulkRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestPersistenceAuditorConfig.class)
@DataJpaTest
public class CouponProductCouponBulkRepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AuditorAware<String> auditorAware;

    private CouponProductCouponBulkRepository repository;

    @BeforeEach
    void setUp() {
        repository = new CouponProductCouponBulkRepositoryImpl(dataSource, auditorAware);
    }

    @Test
    void insertTest() {
        // given
        // 50개 저장
        List<CouponProductCoupon> entities = IntStream.rangeClosed(1, 50)
                .mapToObj(i -> new CouponProductCoupon(100L + i, "010-1111-1111"))
                .toList();

        // when
        repository.saveAll(entities);

        // then
        List<Long> ids = jdbcTemplate.queryForList("SELECT id FROM coupon_product_coupon", Long.class);
        assertThat(ids).hasSize(50);

        // 저장된 엔티티의 id가 1씩 증가하는지 검증
        for (int i = 0; i < entities.size() - 1; i++) {
            long current = entities.get(i).getId();
            long next = entities.get(i + 1).getId();

            assertThat(next - current)
                    .as("index %s: %s -> %s", i, current, next)
                    .isEqualTo(1);
        }
    }
}
