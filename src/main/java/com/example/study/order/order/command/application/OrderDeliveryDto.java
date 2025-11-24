package com.example.study.order.order.command.application;

public record OrderDeliveryDto(
        String address,
        String contact
) {}