package com.example.study.member.command.domain;

import java.util.UUID;

public interface OrderWalletClient {
    void createWallet(UUID memberId);
}
