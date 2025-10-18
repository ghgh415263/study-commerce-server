package com.example.study.order.command.infra;

import com.example.study.common.authentication.UnauthenticatedException;
import com.example.study.order.command.domain.CouponIssuance;
import com.example.study.order.command.domain.CouponIssuanceBulkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CouponIssuanceBulkJdbcTemplateRepository implements CouponIssuanceBulkRepository {

    private final JdbcTemplate jdbcTemplate;
    private final AuditorAware<String> auditorAware;

    private static final String SQL = """
        INSERT INTO coupon_issuance 
            (order_item_id, coupon_code, status, contact, issued_at, created_by, modified_by)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

    @Override
    public void saveAll(List<CouponIssuance> couponIssuances) {
        String currentAuditor = auditorAware.getCurrentAuditor()
                .orElseThrow(UnauthenticatedException::new);

        jdbcTemplate.batchUpdate(SQL, couponIssuances, couponIssuances.size(),
                (ps, entity) -> {
                    ps.setLong(1, entity.getOrderItemId());
                    ps.setString(2, entity.getCouponCode());
                    ps.setString(3, entity.getStatus().name());
                    ps.setObject(4, entity.getContact());
                    ps.setObject(5, entity.getIssuedAt());
                    ps.setString(6, currentAuditor); // created_by
                    ps.setString(7, currentAuditor); // modified_by
                });
    }
}
