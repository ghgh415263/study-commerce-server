package com.example.study.member.command.infra;

import com.example.study.member.command.domain.OrderWalletClient;
import com.example.study.order.command.domain.Wallet;
import com.example.study.order.command.domain.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderWallectClientImpl implements OrderWalletClient {

    private final WalletRepository walletRepository;

    public void createWallet(Long memberId){
        Wallet wallet = new Wallet(memberId);
        walletRepository.save(wallet);
    }
}
