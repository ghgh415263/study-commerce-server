package com.example.study.order.couponproductcoupon.command;

import java.util.List;

public interface CouponProductCouponBulkRepository {
    void saveAll(List<CouponProductCoupon> couponProductCoupons);
}
