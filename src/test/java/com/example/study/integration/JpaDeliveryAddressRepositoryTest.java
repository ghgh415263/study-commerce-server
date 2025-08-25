package com.example.study.integration;

import com.example.study.order.command.domain.AddressVO;
import com.example.study.order.command.domain.DeliveryAddress;
import com.example.study.order.command.domain.DeliveryAddressRepository;
import com.example.study.order.command.infra.JpaDeliveryAddressRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Import(TestPersistenceAuditorConfig.class)
@DataJpaTest
public class JpaDeliveryAddressRepositoryTest {

    @Autowired
    private EntityManager em;

    private DeliveryAddressRepository deliveryAddressRepository;

    @BeforeEach
    void setUp() {
        deliveryAddressRepository = new JpaDeliveryAddressRepository(em);
    }

    @Test
    @DisplayName("DeliveryAddress 저장")
    void saveNewDeliveryAddress() {
        // given
        UUID memberId = UUID.randomUUID();
        AddressVO address = new AddressVO("06000", "서울 강남구", "101호");
        DeliveryAddress deliveryAddress = new DeliveryAddress(memberId, "우리집", address);

        // when
        DeliveryAddress saved = deliveryAddressRepository.save(deliveryAddress);

        // 영속성 컨텍스트 초기화 (flush + clear)
        em.flush();
        em.clear();

        // then
        DeliveryAddress foundEntity = em.find(DeliveryAddress.class, saved.getId());

        assertThat(foundEntity.getId()).isEqualTo(saved.getId());
        assertThat(foundEntity.getMemberId()).isEqualTo(saved.getMemberId());
        assertThat(foundEntity.getName()).isEqualTo(saved.getName());
        assertThat(foundEntity.getAddress()).isEqualTo(saved.getAddress());
    }
}