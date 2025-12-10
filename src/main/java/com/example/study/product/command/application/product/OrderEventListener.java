package com.example.study.product.command.application.product;

import com.example.study.order.order.command.domain.OrderCanceledEvent;
import com.example.study.product.command.domain.product.Product;
import com.example.study.product.command.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final ProductRepository productRepository;

    /**
     * 주문 취소 시점에 상품 재고를 원복하는 리스너.
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handle(OrderCanceledEvent event) {

        Map<Long, Integer> quantities =
                event.getOrderItems().stream()
                        .collect(Collectors.groupingBy(
                                OrderCanceledEvent.OrderCanceledEventItem::getProductId,
                                Collectors.summingInt(OrderCanceledEvent.OrderCanceledEventItem::getQuantity)
                        ));

        List<Product> products = productRepository.findAllByIdIn(quantities.keySet().stream().toList());

        products.forEach(product -> product.increaseStock(quantities.get(product.getId())));
    }
}
