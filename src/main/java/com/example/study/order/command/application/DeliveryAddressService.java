package com.example.study.order.command.application;

import com.example.study.order.command.domain.AddressVO;
import com.example.study.order.command.domain.DeliveryAddress;
import com.example.study.order.command.domain.DeliveryAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DeliveryAddressService {

    private final DeliveryAddressRepository deliveryAddressRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long saveDeliveryAddress(Long memberId, DeliveryAddressDto dto) {
        AddressVO addressVO = new AddressVO(
                dto.zipCode(),
                dto.baseAddress(),
                dto.detailAddress()
        );

        DeliveryAddress deliveryAddress = new DeliveryAddress(memberId, dto.name(), addressVO);

        return deliveryAddressRepository.save(deliveryAddress).getId();
    }

    @Transactional
    public void modifyDeliveryAddress(DeliveryAddressRequestDto dto) {
        DeliveryAddress modifyDeliveryAddress = deliveryAddressRepository.findById(dto.id())
                .orElseThrow(DeliveryAddressNotFoundException::new);

        AddressVO addressVO = new AddressVO(
            dto.zipCode(),
            dto.baseAddress(),
            dto.detailAddress()
        );

        modifyDeliveryAddress.updateDeliveryAddress(dto.name(), addressVO);
    }
}