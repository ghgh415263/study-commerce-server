package com.example.study.order.couponproductcoupon.command;

import com.example.study.common.event.DomainEventBundle;

import java.util.List;

public interface CouponProductCouponEventPublisher {

    /**
     * CouponProductCoupon 단일 어그리거트에 대한 도메인 이벤트 리스트를 Outbox 테이블에 저장합니다.
     *
     * @param aggregateType 어그리거트 루트 타입명
     * @param aggregateId 이벤트 메시지 키로 사용할 대상 식별자 (ex. 주문 ID)
     * @param eventPayloads 실제 이벤트 메시지 페이로드 객체 리스트
     */
    <T extends CouponProductCouponDomainEvent> void publish(String aggregateType, String aggregateId, List<T> eventPayloads);

    /**
     * 여러 개의 DomainEventBundle 을 받아 하나의 트랜잭션 안에서
     * 모두 Outbox 이벤트로 변환하여 벌크 저장합니다.
     *
     * @param bundles 여러 DomainEventBundle 묶음
     * @param <T>     CouponProductCoupon 도메인 이벤트 타입
     */
    <T extends CouponProductCouponDomainEvent> void publishBulk(List<DomainEventBundle<T>> bundles);
}
