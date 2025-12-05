package com.example.study.order.order.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.fo.Authentication;
import com.example.study.common.lock.LockTemplate;
import com.example.study.order.order.command.application.CreateOrderDto;
import com.example.study.order.order.command.application.CreateOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class CreateOrderController {

    private final LockTemplate lockTemplate;

    private final CreateOrderService createOrderService;

    @PostMapping
    public ApiSuccessResponse<Void> save(@Valid @RequestBody CreateOrderDto dto, Authentication authentication) {

        String lockName = "createOrder-lock:" + authentication.getMemberId();

        lockTemplate.executeWithLock(lockName, () -> createOrderService.createOrder(dto, authentication.getMemberId()));

        return ApiSuccessResponse.empty();
    }
}
