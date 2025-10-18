package com.example.study.order.command.application;

import com.example.study.order.command.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderCreateService {

    private final OrderRepository orderRepository;
    private final WalletRepository walletRepository;
    private final ProductClient productClient;
    private final PaymentRepository paymentRepository;
    private final DeliveryRepository deliveryRepository;

    @Transactional
    public Long createOrder(CreateOrderDto dto, UUID memberId) {

        // 상품 ID 추출 + 가격 조회
        List<Long> productIds = dto.items().stream()
                .map(OrderItemDto::productId)
                .toList();

        Map<Long, ProductClient.OrderedProduct> priceMap = productClient.getOrderedProducts(productIds)
                .stream()
                .collect(Collectors.toMap(ProductClient.OrderedProduct::productId, p -> p));

        // OrderItem 생성
        List<OrderItem> items = dto.items().stream()
                .map(itemDto -> {
                    BigDecimal price = BigDecimal.valueOf(priceMap.get(itemDto.productId()).price());
                    return OrderItem.of(itemDto.productId(), itemDto.quantity(), price);
                })
                .toList();

        // 재고 감소
        List<ProductClient.ProductStockRequest> stockRequests = items.stream()
                .map(i -> new ProductClient.ProductStockRequest(i.getProductId(), i.getQuantity()))
                .toList();
        productClient.decreaseStocks(stockRequests);

        // 주문 생성
        Order order = new Order(memberId, LocalDateTime.now(), items);
        orderRepository.save(order);

        // 총 금액 계산
        BigDecimal totalPrice = items.stream()
                .map(i -> i.getPriceAtOrder().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Wallet에서 금액 차감
        Wallet wallet = walletRepository.findByMemberId(memberId)
                .orElseThrow(WalletNotFoundException::new);
        wallet.withdraw(totalPrice);

        // 결제 생성
        Payment payment = new Payment(totalPrice, LocalDateTime.now(), order.getId());
        paymentRepository.save(payment);

        List<OrderItem> deliveryOrderItems = items.stream()
                .filter(i -> "DELIVERY".equals(priceMap.get(i.getProductId()).productType()))
                .toList();

        if (!deliveryOrderItems.isEmpty()) {
            Delivery delivery = new Delivery(DeliveryStatus.NOT_STARTED, dto.delivery().address(), dto.delivery().contact());
            List<Long> deliveryOrderItemIds = deliveryOrderItems.stream()
                    .map(OrderItem::getId)
                    .toList();
            delivery.addDeliveryItems(deliveryOrderItemIds);
            deliveryRepository.save(delivery);
        }

        return order.getId();
    }
}