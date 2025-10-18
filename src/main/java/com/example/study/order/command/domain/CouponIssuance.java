package com.example.study.order.command.domain;

import com.example.study.common.InvalidArgumentException;
import com.example.study.common.persistance.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponIssuance extends BaseUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long orderItemId;

    @Column(nullable = false, length = 100, unique = true)
    private String couponCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CouponIssuanceStatus status;

    @Column(nullable = false, length = 50)
    private String contact;

    private LocalDateTime issuedAt;

    public CouponIssuance(Long orderItemId, String contact) {
        if (contact == null) {
            throw new InvalidArgumentException("쿠폰을 발급할 연락처를 입력해주세요.");
        }
        this.orderItemId = orderItemId;
        this.contact = contact;
        this.couponCode = generateRandomCouponCode();
        this.status = CouponIssuanceStatus.READY;
    }

    private String generateRandomCouponCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
