package com.example.study.order.couponproductcoupon.command;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponProductCouponCreatedEvent implements CouponProductCouponDomainEvent {

    private final Long couponProductCouponId;
    private final Long orderItemId;
    private final String couponCode;
    private final String contact;
    private final LocalDateTime issuedAt;

    public CouponProductCouponCreatedEvent(CouponProductCoupon couponProductCoupon) {
        this.couponProductCouponId = couponProductCoupon.getId();
        this.orderItemId = couponProductCoupon.getOrderItemId();
        this.couponCode = couponProductCoupon.getCouponCode();
        this.contact = couponProductCoupon.getContact();
        this.issuedAt = couponProductCoupon.getIssuedAt();
    }
}
