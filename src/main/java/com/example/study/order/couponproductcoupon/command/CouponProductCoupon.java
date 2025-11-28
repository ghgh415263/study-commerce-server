package com.example.study.order.couponproductcoupon.command;

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
public class CouponProductCoupon extends BaseUpdateEntity {

    @Id
    @Column(name = "coupon_product_coupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orderItemId;

    @Column(nullable = false, length = 100, unique = true)
    private String couponCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CouponProductCouponStatus status;

    @Column(nullable = false, length = 20)
    private String contact;

    private LocalDateTime issuedAt;

    public CouponProductCoupon(Long orderItemId, String contact) {
        if (orderItemId == null || orderItemId <= 0) {
            throw new InvalidArgumentException("잘못된 주문 상품 정보입니다.");
        }

        if (contact == null) {
            throw new InvalidArgumentException("쿠폰을 발급할 연락처를 입력해주세요.");
        }
        this.orderItemId = orderItemId;
        this.contact = contact;
        this.couponCode = generateRandomCouponCode();
        this.status = CouponProductCouponStatus.READY;
        this.issuedAt = LocalDateTime.now();
    }

    private String generateRandomCouponCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}
