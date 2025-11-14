package com.example.study.order.order.command.application;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record CreateOrderDto(
        @NotEmpty(message = "주문 아이템은 최소 1개 이상이어야 합니다.")
        @Valid
        List<OrderItemDto> items,

        OrderDeliveryDto delivery,

        @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$", message = "연락처는 010-1234-5678 형식이어야 합니다.")
        String couponIssueContact
) {}
