package com.example.study.order.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssuanceRepository extends JpaRepository<CouponIssuance, Long> {
}
