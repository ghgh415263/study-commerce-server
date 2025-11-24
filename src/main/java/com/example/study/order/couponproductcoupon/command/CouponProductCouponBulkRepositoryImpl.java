package com.example.study.order.couponproductcoupon.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;
import com.example.study.common.authentication.fo.UnauthenticatedException;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CouponProductCouponBulkRepositoryImpl implements CouponProductCouponBulkRepository {

    private static final String SQL = """
        INSERT INTO coupon_product_coupon
            (order_item_id, coupon_code, status, contact, issued_at, created_by, modified_by)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

    private final DataSource dataSource;
    private final AuditorAware<String> auditorAware;

    @Override
    public void saveAll(List<CouponProductCoupon> list) {

        if (list.isEmpty()) return;

        String auditor = auditorAware.getCurrentAuditor()
                .orElseThrow(UnauthenticatedException::new);

        Connection con = DataSourceUtils.getConnection(dataSource);

        try (PreparedStatement ps = con.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            for (CouponProductCoupon e : list) {
                ps.setLong(1, e.getOrderItemId());
                ps.setString(2, e.getCouponCode());
                ps.setString(3, e.getStatus().name());
                ps.setString(4, e.getContact());
                ps.setObject(5, e.getIssuedAt());
                ps.setString(6, auditor);
                ps.setString(7, auditor);
                ps.addBatch();
            }

            ps.executeBatch();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                int i = 0;
                while (rs.next()) {
                    Long id = rs.getLong(1);
                    Object entity = list.get(i++);
                    setIdByReflection(entity, id);
                }
            }

        } catch (SQLException e) {
            log.error("DB 오류 발생: {}", e.getMessage(), e);
            throw new DatabaseAccessException("DB 오류", e);
        }
    }

    private void setIdByReflection(Object entity, Long id) {
        try {
            Field field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("엔티티에 id 필드가 없거나 접근할 수 없습니다: "
                    + entity.getClass().getName(), e);
        }
    }
}
