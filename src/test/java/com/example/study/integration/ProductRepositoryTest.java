package com.example.study.integration;

import com.example.study.product.command.application.product.ProductNotFoundException;
import com.example.study.product.command.domain.product.*;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Import(TestPersistenceAuditorConfig.class)
@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    // Test object for ProductRepositoryTest
    private static DeliveryProduct getDeliveryProduct() {
        DeliveryProduct deliveryProduct = new DeliveryProduct("일리머신&커피캡슐"
                , 230000
                , 2
                ,"일리배송형 커피머신입니다."
                , ProductStatus.SOLD_OUT.name()
                , 1000
                , 10);
        List<ProductTag> ProductTagList = new ArrayList<>(
                Arrays.asList(
                        new ProductTag("일리"),
                        new ProductTag("커피캡슐")
                )
        );
        deliveryProduct.assignProductTags(ProductTagList);
        return deliveryProduct;
    }

    @Test
    @DisplayName("배송형 상품을 저장한다")
    void saveDeliveryProduct() throws ProductNotFoundException {
        // given
        DeliveryProduct deliveryProduct = getDeliveryProduct();

        // when
        Product saved = productRepository.save(deliveryProduct);

        // 영속성 컨텍스트 초기화 (flush + clear)
        em.flush();
        em.clear();

        // then
        DeliveryProduct foundEntity = productRepository.findDeliveryProductById(saved.getId()).orElseThrow(ProductNotFoundException::new);

        assertThat(foundEntity.getName()).isEqualTo("일리머신&커피캡슐");
        assertThat(foundEntity.getPrice()).isEqualTo(230000);
        assertThat(foundEntity.getStockQuantity()).isEqualTo(2);
        assertThat(foundEntity.getDescription()).isEqualTo("일리배송형 커피머신입니다.");
        assertThat(foundEntity.getProductStatus().toString()).isEqualTo("SOLD_OUT");
        assertThat(foundEntity.getFee()).isEqualTo(1000);
        assertThat(foundEntity.getWeight()).isEqualTo(10);
        assertThat(foundEntity.getDescription()).isEqualTo("일리배송형 커피머신입니다.");
        List<String> tagList = foundEntity.getProductTags().stream().map(ProductTag::getTagName).collect(Collectors.toList());
        AssertionsForInterfaceTypes.assertThat(tagList).contains("일리", "커피캡슐");
    }

    @Test
    @DisplayName("배송형 상품을 수정한다")
    void updateDeliveryProduct() throws ProductNotFoundException {
        // given
        DeliveryProduct deliveryProduct = getDeliveryProduct();

        // when
        Product saved = productRepository.save(deliveryProduct);

        // 영속성 컨텍스트 초기화 (flush + clear)
        em.flush();
        em.clear();

        // when
        DeliveryProduct newDeliverProduct = productRepository.findDeliveryProductById(saved.getId()).orElseThrow(ProductNotFoundException::new);

        newDeliverProduct.update(
                "네스프레소 머신"
                ,310000
                , 1
                , "네스프레소 머신입니다"
                , "ON_SALE");

        List<ProductTag> newTags = new ArrayList<>();
        ProductTag newPt1 = new ProductTag("네스프레소");
        ProductTag newPt2 = new ProductTag("커피머신");
        newTags.add(newPt1);
        newTags.add(newPt2);
        newDeliverProduct.updateProductTags(newDeliverProduct, newTags);

        newDeliverProduct.assignFee(2000);
        newDeliverProduct.assignWeight(20);

        // then
        DeliveryProduct foundEntity = productRepository.findDeliveryProductById(saved.getId()).orElseThrow(ProductNotFoundException::new);

        assertThat(foundEntity.getName()).isEqualTo("네스프레소 머신");
        assertThat(foundEntity.getPrice()).isEqualTo(310000);
        assertThat(foundEntity.getStockQuantity()).isEqualTo(1);
        assertThat(foundEntity.getDescription()).isEqualTo("네스프레소 머신입니다");
        assertThat(foundEntity.getProductStatus().toString()).isEqualTo("ON_SALE");
        assertThat(foundEntity.getFee()).isEqualTo(2000);
        assertThat(foundEntity.getWeight()).isEqualTo(20);
        List<String> tagList = foundEntity.getProductTags().stream().map(ProductTag::getTagName).collect(Collectors.toList());
        AssertionsForInterfaceTypes.assertThat(tagList).contains("네스프레소", "커피머신");
    }

    @Test
    @DisplayName("배송형 상품을 삭제한다")
    void deleteDeliveryProduct() {
        // given
        DeliveryProduct deliveryProduct = getDeliveryProduct();

        // when
        Product saved = productRepository.save(deliveryProduct);

        // 영속성 컨텍스트 초기화 (flush + clear)
        em.flush();
        em.clear();

        // when
        DeliveryProduct deleteProduct = productRepository.findDeliveryProductById(saved.getId()).orElseThrow(ProductNotFoundException::new);
        productRepository.delete(deleteProduct);

        // 영속성 컨텍스트 초기화 (flush + clear)
        em.flush();
        em.clear();

        // then
        assertThatThrownBy(() ->
                productRepository.findProductById(saved.getId())
                        .orElseThrow(ProductNotFoundException::new)
        )
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("해당 상품정보를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("BackOffice Product 수정(조회)시에는 DB에 Lock을 건다(비관락)")
    void testProductFindLock() throws InterruptedException {
        // given
        DeliveryProduct deliveryProduct = getDeliveryProduct();

        // when
        Product saved = productRepository.save(deliveryProduct);

        // 영속성 컨텍스트 초기화 (flush + clear)
        em.flush();
        em.clear();

        // when
        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> {
            transactionTemplate.executeWithoutResult(status -> {
                productRepository.findDeliveryProductById(saved.getId())
                        .orElseThrow(ProductNotFoundException::new);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        });

        Thread.sleep(500);



        Future<?> future = executor.submit(() -> {
            transactionTemplate.executeWithoutResult(status -> {
                em.createNativeQuery("SET innodb_lock_wait_timeout = 3").executeUpdate(); // 락 타임 아웃 3초 설정
                productRepository.findDeliveryProductById(saved.getId())
                        .orElseThrow(ProductNotFoundException::new);
            });
        });

        executor.shutdown();

        // then
        // 락 대기 발생 여부 검증
        //  Hibernate 의 PessimisticLockException 은 Spring 이 자동으로 PessimisticLockingFailureException 으로 감싸서 던진다.
        assertThatThrownBy(future::get)
                .isInstanceOf(ExecutionException.class)
                .cause()
                .isInstanceOf(PessimisticLockingFailureException.class);

    }
}
