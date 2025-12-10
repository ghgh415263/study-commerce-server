package com.example.study.order.order.command.application;

import com.example.study.order.order.command.domain.*;
import com.example.study.order.payment.Payment;
import com.example.study.order.payment.PaymentRepository;
import com.example.study.order.payment.PaymentNotFoundException;
import com.example.study.order.wallet.Wallet;
import com.example.study.order.wallet.WalletNotFoundException;
import com.example.study.order.wallet.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderCancelService {

    private final OrderRepository orderRepository;

    private final DeliveryRepository deliveryRepository;

    private final OrderCancelRepository orderCancelRepository;

    private final WalletRepository walletRepository;

    private final PaymentRepository paymentRepository;

    private final OrderDomainEventPublisher orderDomainEventPublisher;

    @Transactional
    public void cancelOrder(Long memberId, CancelOrderDto dto, Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if (!memberId.equals(order.getMemberId())) {
            throw new OrderAccessException("잘못된 주문 정보입니다.");
        }

        order.cancel();

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(DeliveryNotFoundException::new);

        delivery.cancel();

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(PaymentNotFoundException::new);

        payment.cancel();

        Wallet wallet = walletRepository.findByMemberId(memberId)
                .orElseThrow(WalletNotFoundException::new);

        wallet.deposit(payment.getAmount());

        orderDomainEventPublisher.publish(new OrderCanceledEvent(order));

        orderCancelRepository.save(new OrderCancel(orderId, dto.reasonCode(), dto.detailReason()));
    }
}
