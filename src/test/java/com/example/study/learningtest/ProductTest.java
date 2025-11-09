package com.example.study.learningtest;

import com.example.study.integration.TestPersistenceAuditorConfig;
import com.example.study.product.command.domain.product.DuplicateProductTagsException;
import com.example.study.product.command.application.product.ProductNotFoundException;
import com.example.study.product.command.domain.product.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PessimisticLockException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Import(TestPersistenceAuditorConfig.class)
@DataJpaTest
public class ProductTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    // 테스트 객체
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
        assertThat(tagList).contains("일리", "커피캡슐");
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
        assertThat(tagList).contains("네스프레소", "커피머신");
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
                productRepository.findDeliveryProductById(saved.getId())
                        .orElseThrow(ProductNotFoundException::new);
            });
        });

        executor.shutdown();

        // ProductRepository -> SpringData Jpa 로 변경되면서 수정사항 :
        // EntityManager는 진짜 즉시 DB 쿼리를 실행하고 락을 잡지만, Repository는 내부에서 트랜잭션/flush 타이밍을 늦게 처리한다.
        // QueryHints의 javax.persistence.lock.timeout은 표준이지만, H2/MySQL 테스트에서는 무시되어 PessimisticLockException 을 관찰하기 어렵다.

        // then - 락 대기 발생 여부 검증
        long start = System.currentTimeMillis();
        assertThatThrownBy(() -> {
            future.get(3, TimeUnit.SECONDS); // 3초 기다려도 안 끝나면 TimeoutException
        }).isInstanceOf(TimeoutException.class);
        long elapsed = System.currentTimeMillis() - start;

        System.out.println("락 대기 감지 시간: " + elapsed + "ms");

        // 락으로 인해 블로킹이 발생했다면 3초 근처에서 Timeout 발생 → 정상
        assertThat(elapsed).isGreaterThanOrEqualTo(2900);
    }

    @Test
    @DisplayName("중복 상품 태그 함수를 입력하면 DuplicateProductTagsException 예외가 발생한다")
    void saveDuplicateProductTag(){
        // given
        DeliveryProduct deliveryProduct = new DeliveryProduct("일리머신&커피캡슐"
                , 230000
                , 2
                ,"일리배송형 커피머신입니다."
                , ProductStatus.SOLD_OUT.name()
                , 1000
                , 10);

        //when
        List<ProductTag> ProductTagList = List.of(
                new ProductTag("일리"),
                new ProductTag("커피머신"),
                new ProductTag("일리상품"),
                new ProductTag("일리"),
                new ProductTag("커피머신")
        );

        // then
        assertThatThrownBy( () -> deliveryProduct.assignProductTags(ProductTagList))
                .isInstanceOf(DuplicateProductTagsException.class)
                .hasMessageContaining("상품 태그는 중복으로 등록할 수 없습니다. 중복 태그 : ");
    }

    @Test
    @DisplayName("이미 등록된 상품 태그를 등록하려고 하면")
    void addDuplicateProductTag(){
        // given
        DeliveryProduct deliveryProduct = new DeliveryProduct("일리머신&커피캡슐"
                , 230000
                , 2
                ,"일리배송형 커피머신입니다."
                , ProductStatus.SOLD_OUT.name()
                , 1000
                , 10);

        //when
        List<ProductTag> ProductTagList = List.of(
                new ProductTag("일리"),
                new ProductTag("커피머신"),
                new ProductTag("일리상품")
        );
        deliveryProduct.assignProductTags(ProductTagList);

        // then
        assertThatThrownBy(() -> deliveryProduct.assignProductTags(List.of(new ProductTag("일리"),new ProductTag("커피머신"))))
                .isInstanceOf(DuplicateProductTagsException.class)
                .hasMessageContaining("상품 태그는 중복으로 등록할 수 없습니다. 중복 태그 : ");
    }
}