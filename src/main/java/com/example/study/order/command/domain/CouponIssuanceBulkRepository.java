package com.example.study.order.command.domain;

import java.util.List;

public interface CouponIssuanceBulkRepository {
    void saveAll(List<CouponIssuance> couponIssuances);
}
