package com.example.study.order.couponproductcoupon.command;

import com.example.study.common.event.DomainEventBundle;
import com.example.study.order.order.command.domain.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponProductCouponEventListener {

    private final CouponProductCouponBulkRepository couponProductCouponBulkRepository;

    private final CouponProductCouponEventPublisher couponProductCouponEventPublisher;

    /**
     * 주문 생성 이벤트(OrderCreatedEvent)가 발행되었을 때,
     * 쿠폰형 상품에 대한 쿠폰 발행 엔티티(CouponProductCoupon)를 생성하는 이벤트 리스너
     *
     * <p> @TransactionalEventListener(BEFORE_COMMIT) 사용 이유
     * <ul>
     *   <li>주문 생성 트랜잭션(createOrder)의 커밋 직전에 실행되어</li>
     *   <li>쿠폰 생성 로직도 동일한 트랜잭션에 포함시키기 위함</li>
     * </ul>
     *
     * <p> 처리 흐름
     * <ol>
     *   <li>OrderCreatedEvent 내 OrderItem 중 productType=COUPON 인 항목 필터링</li>
     *   <li>상품 수량(quantity)만큼 CouponProductCoupon 엔티티 생성</li>
     *   <li>이 모든 작업은 주문 생성 트랜잭션 안에서 수행됨</li>
     * </ol>
     *
     * @param orderCreatedEvent 주문 생성 도메인 이벤트(주문 저장 후 발행)
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(OrderCreatedEvent orderCreatedEvent){

        var couponItems = orderCreatedEvent.getOrderItems().stream()
                .filter(item -> item.getProductType().equals("COUPON"))
                .toList();

        if (couponItems.isEmpty())
            return;

        List<CouponProductCoupon> couponProductCouponList = new ArrayList<>();
        couponItems.forEach(item -> {
            for (int i = 0; i < item.getQuantity(); i++) {
                couponProductCouponList.add(new CouponProductCoupon(item.getId(), orderCreatedEvent.getCouponIssueContact()));
            }
        });
        couponProductCouponBulkRepository.saveAll(couponProductCouponList);

        List<DomainEventBundle<CouponProductCouponCreatedEvent>> eventBundles = couponProductCouponList.stream()
                .map(ci ->
                        new DomainEventBundle<>("CouponProductCoupon", ci.getId().toString(),
                                List.of(new CouponProductCouponCreatedEvent(ci))))
                .toList();
        couponProductCouponEventPublisher.publishBulk(eventBundles);

    }
}
