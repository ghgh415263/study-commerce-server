package com.example.study.order.command.application;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderDto(
        @NotEmpty(message = "주문 아이템은 최소 1개 이상이어야 합니다.")
        @Valid
        List<OrderItemDto> items,

        @NotNull(message = "배송 정보는 필수입니다.")
        @Valid
        OrderDeliveryDto delivery
) {}
