# Order Bounded Context

주문(Order) 영역에서 사용하는 도메인 용어, 엔티티명, 필드명, 이벤트명 등을 표준화한 네이밍 사전입니다.

---

### - 엔티티

| 개념 | 영어 | 설명 | 예시 네이밍 |
|------|------|------|--------------|
| 주문 | Order | 고객의 구매 의사가 반영된 거래 단위 | `Order` |
| 주문상품 | OrderItem | 주문에 포함된 개별 상품 단위 | `OrderItem` |
| 배송 | Delivery | 주문 상품의 물류 이동 및 묶음 배송 단위 | `Delivery` |
| 배송상품 | DeliveryItem | 배송을 구성하는 최소 단위로, 각 DeliveryItem은 정확히 하나의 OrderItem을 표현하며 여러 DeliveryItem이 모여 하나의 Delivery(배송 묶음)를 형성함 | `DeliveryItem` |
| 결제 | Payment | 주문에 대한 결제 승인 정보 | `Payment` |
| 쿠폰형 상품 쿠폰 | CouponProductCoupon | 쿠폰형 상품 주문 시 생성되는 실제 쿠폰 엔티티. OrderItem 기반으로 생성되며 상태·발급코드·연락처 등 속성을 포함함 | `CouponProductCoupon` |
| 지갑 | Wallet | 고객이 주문 시 사용할 수 있는 내부 잔액(예치금/포인트) 관리 엔티티 | `Wallet` |

