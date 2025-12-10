package com.example.study.order.order.command.application;

import com.example.study.order.order.command.domain.*;
import com.example.study.order.payment.Payment;
import com.example.study.order.payment.PaymentRepository;
import com.example.study.order.wallet.Wallet;
import com.example.study.order.wallet.WalletNotFoundException;
import com.example.study.order.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreateOrderService {

    private final OrderRepository orderRepository;
    private final WalletRepository walletRepository;
    private final ProductClient productClient;
    private final PaymentRepository paymentRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderDomainEventPublisher orderDomainEventPublisher;

    @Transactional
    public Long createOrder(CreateOrderDto dto, Long memberId) {

        // 상품 ID 추출 + 가격 조회
        List<Long> productIds = dto.items().stream()
                .map(OrderItemDto::productId)
                .toList();

        // 주문된 상품 정보 가져옴
        List<ProductClient.OrderedProduct> orderedProducts = productClient.getOrderedProducts(productIds);
        if (productIds.size() != orderedProducts.size()) {
            throw new OrderedProductNotFoundException(productIds);
        }

        Map<Long, ProductClient.OrderedProduct> priceMap = orderedProducts
                .stream()
                .collect(Collectors.toMap(ProductClient.OrderedProduct::productId, p -> p));

        // OrderItem 생성
        List<OrderItem> items = dto.items().stream()
                .map(itemDto -> {
                    BigDecimal price = BigDecimal.valueOf(priceMap.get(itemDto.productId()).price());
                    String productType = priceMap.get(itemDto.productId()).productType();
                    String productName = priceMap.get(itemDto.productId()).name();
                    return OrderItem.of(itemDto.productId(), productName, price, productType, itemDto.quantity());
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
        BigDecimal totalPrice = order.calculateTotalPrice();

        // Wallet에서 금액 차감
        Wallet wallet = walletRepository.findByMemberId(memberId)
                .orElseThrow(WalletNotFoundException::new);
        wallet.withdraw(totalPrice);

        // 결제 생성
        Payment payment = new Payment(totalPrice, LocalDateTime.now(), order.getId());
        paymentRepository.save(payment);

        Map<String, List<OrderItem>> groupedItems = items.stream()
                .collect(Collectors.groupingBy(
                        i -> priceMap.get(i.getProductId()).productType()
                ));

        List<OrderItem> deliveryItems = groupedItems.getOrDefault("DELIVERY", List.of());

        // 배송형 상품이 존재하면 배송 생성
        if (!deliveryItems.isEmpty()) {
            Delivery delivery = new Delivery(dto.delivery().address(), dto.delivery().contact(), order.getId());
            List<Long> deliveryOrderItemIds = deliveryItems.stream()
                    .map(OrderItem::getId)
                    .toList();
            delivery.addDeliveryItems(deliveryOrderItemIds);
            deliveryRepository.save(delivery);
        }

        orderDomainEventPublisher.publish(new OrderCreatedEvent(totalPrice, order, dto.couponIssueContact()));

        return order.getId();
    }
}