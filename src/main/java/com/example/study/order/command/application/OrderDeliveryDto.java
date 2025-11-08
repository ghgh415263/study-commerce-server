package com.example.study.order.command.application;

public record OrderDeliveryDto(
        String address,
        String contact
) {}