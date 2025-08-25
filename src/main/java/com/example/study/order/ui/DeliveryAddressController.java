package com.example.study.order.ui;

import com.example.study.common.authentication.AuthenticationContext;
import com.example.study.common.lock.LockTemplate;
import com.example.study.order.command.application.DeliveryAddressDto;
import com.example.study.order.command.application.DeliveryAddressRequestDto;
import com.example.study.order.command.application.DeliveryAddressService;
import com.example.study.common.ApiSuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders/delivery-addresses")
public class DeliveryAddressController {

	private final DeliveryAddressService deliveryAddressService;

	private final LockTemplate lockTemplate;

	private final AuthenticationContext authenticationContext;

	@PostMapping
	public ApiSuccessResponse<Void> save(@Valid @RequestBody DeliveryAddressDto dto) {

		String lockName = "deliveryAddress-lock:" + authenticationContext.getAuthentication().getMemberId();

		lockTemplate.executeWithLock(lockName, () -> deliveryAddressService.saveDeliveryAddress(dto));

		return ApiSuccessResponse.empty();
	}

	@PutMapping
	public ApiSuccessResponse<Void> modify(@Valid @RequestBody DeliveryAddressRequestDto dto){
		deliveryAddressService.modifyDeliveryAddress(dto);
		return ApiSuccessResponse.empty();
	}
}
