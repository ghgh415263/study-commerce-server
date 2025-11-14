package com.example.study.learningtest;

import com.example.study.integration.TestPersistenceAuditorConfig;
import com.example.study.order.order.command.application.DeliveryAddressNotFoundException;
import com.example.study.order.order.command.domain.AddressVO;
import com.example.study.order.order.command.domain.DeliveryAddress;
import com.example.study.order.order.command.domain.DeliveryAddressRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Import(TestPersistenceAuditorConfig.class)
@DataJpaTest
public class DeliveryAddressTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    @Test
    @DisplayName("DeliveryAddress를 수정한다")
    void updateNewDeliveryAddress() {
        // given
        AddressVO address = new AddressVO("06000", "서울 강남구", "101호");
        DeliveryAddress deliveryAddress = new DeliveryAddress(1L, "우리집", address);
        DeliveryAddress saved = deliveryAddressRepository.save(deliveryAddress);

        em.flush();
        em.clear();

        // when
        DeliveryAddress changedEntity = deliveryAddressRepository.findById(saved.getId())
                .orElseThrow(DeliveryAddressNotFoundException::new);

        changedEntity.updateDeliveryAddress("상훈네집",
                new AddressVO("38750", "춘천시 명동", "702호"));

        em.flush();
        em.clear();

        // then
        DeliveryAddress foundEntity = deliveryAddressRepository.findById(saved.getId())
                .orElseThrow(DeliveryAddressNotFoundException::new);

        assertThat(foundEntity.getName()).isEqualTo("상훈네집");
        assertThat(foundEntity.getAddress().getZipCode()).isEqualTo("38750");
        assertThat(foundEntity.getAddress().getBaseAddress()).isEqualTo("춘천시 명동");
        assertThat(foundEntity.getAddress().getDetailAddress()).isEqualTo("702호");
    }
}