package com.example.study.order.order.command.domain;

import com.example.study.common.persistance.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Audited
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseUpdateEntity {

    @Id
    @Column(name = "payment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long OrderId;

    // 결제 금액
    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    // 결제 일시
    @Column(nullable = false)
    private LocalDateTime paidAt;

    public Payment(BigDecimal amount, LocalDateTime paidAt, Long OrderId) {
        this.amount = amount;
        this.status = PaymentStatus.COMPLETED;
        this.paidAt = paidAt;
        this.OrderId = OrderId;
    }

}
