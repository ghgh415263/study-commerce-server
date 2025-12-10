package com.example.study.order.order.ui;

import com.example.study.common.ApiSuccessResponse;
import com.example.study.common.authentication.fo.Authentication;
import com.example.study.order.order.command.application.CancelOrderDto;
import com.example.study.order.order.command.application.OrderCancelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderCancelController {

    private final OrderCancelService orderCancelService;

    @PostMapping("{orderId}/cancel")
    public ApiSuccessResponse<Void> cancel(Authentication authentication,
                                           @PathVariable Long orderId,
                                           @RequestBody @Valid CancelOrderDto dto) {

        orderCancelService.cancelOrder(authentication.getMemberId(), dto, orderId);

        return ApiSuccessResponse.empty();
    }
}
