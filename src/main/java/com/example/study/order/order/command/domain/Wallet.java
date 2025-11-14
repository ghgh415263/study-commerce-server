package com.example.study.order.order.command.domain;

import com.example.study.common.InvalidArgumentException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

@Audited
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 회원과 1:1
    @Column(nullable = false, unique = true)
    private Long memberId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance; // 기본 0원

    public Wallet(Long memberId) {
        this.memberId = memberId;
        this.balance = BigDecimal.ZERO;
    }

    // 충전
    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidArgumentException("충전 금액은 0보다 커야 합니다.");
        }
        this.balance = this.balance.add(amount);
    }

    // 사용(차감)
    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidArgumentException("사용 금액은 0보다 커야 합니다.");
        }
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientWalletBalanceException();
        }
        this.balance = this.balance.subtract(amount);
    }
}
