package com.example.study.learningtest;

import com.example.study.integration.TestPersistenceAuditorConfig;
import com.example.study.product.command.application.ProductNotFoundException;
import com.example.study.product.command.domain.*;
import com.example.study.product.command.infra.JpaProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PessimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Import(TestPersistenceAuditorConfig.class)
@DataJpaTest
public class ProductTest {

    @Autowired
    private EntityManager em;

    private ProductRepository productRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        productRepository = new JpaProductRepository(em);
    }

    @Test
    @DisplayName("상품을 저장한다")
    void saveProduct() throws ProductNotFoundException {
        // given
        ProductTag pt1 = new ProductTag("핸드폰");
        ProductTag pt2 = new ProductTag("삼성");
        ProductTag pt3 = new ProductTag("플래그쉽");
        Product product = new Product("갤럭시S25"
                , 1520000
                , 3
                ,"갤럭시 상품입니다");
        product.setProductStatus(ProductStatus.ON_SALE.name());
        product.setProductTags(pt1);
        product.setProductTags(pt2);
        product.setProductTags(pt3);

        // when
        Product saved = productRepository.save(product);

        // 영속성 컨텍스트 초기화 (flush + clear)
        em.flush();
        em.clear();

        // then
        Product foundEntity = productRepository.findByProductId(saved.getId()).orElseThrow(ProductNotFoundException::new);

        assertThat(foundEntity.getName()).isEqualTo("갤럭시S25");
        assertThat(foundEntity.getPrice()).isEqualTo(1520000);
        assertThat(foundEntity.getStockQuantity()).isEqualTo(3);
        assertThat(foundEntity.getDescription()).isEqualTo("갤럭시 상품입니다");
        assertThat(foundEntity.getProductStatus().toString()).isEqualTo("ON_SALE");
        List<String> tagList = foundEntity.getProductTags().stream().map(ProductTag::getTagName).collect(Collectors.toList());
        assertThat(tagList).contains("핸드폰", "삼성", "플래그쉽");
    }

    @Test
    @DisplayName("배송형 상품을 저장한다")
    void saveDeliveryProduct() throws ProductNotFoundException {
        // given
        ProductTag pt1 = new ProductTag("일리");
        ProductTag pt2 = new ProductTag("커피캡슐");
        Product product = new Product("일리머신&커피캡슐"
                , 230000
                , 2
                ,"일리배송형 커피머신입니다.");
        product.setProductStatus(ProductStatus.SOLD_OUT.name());
        product.setProductTags(pt1);
        product.setProductTags(pt2);

        int fee = 1000;
        int weight = 10;

        DeliveryProduct delivertProduct = DeliveryProduct.fromProduct(
                product
                , fee
                , weight);

        // when
        Product saved = productRepository.save(delivertProduct);

        // 영속성 컨텍스트 초기화 (flush + clear)
        em.flush();
        em.clear();

        // then
        Product foundEntity = productRepository.findByProductId(saved.getId()).orElseThrow(ProductNotFoundException::new);

        assertThat(foundEntity.getName()).isEqualTo("일리머신&커피캡슐");
        assertThat(foundEntity.getPrice()).isEqualTo(230000);
        assertThat(foundEntity.getStockQuantity()).isEqualTo(2);
        assertThat(foundEntity.getDescription()).isEqualTo("일리배송형 커피머신입니다.");
        assertThat(foundEntity.getProductStatus().toString()).isEqualTo("SOLD_OUT");
        List<String> tagList = foundEntity.getProductTags().stream().map(ProductTag::getTagName).collect(Collectors.toList());
        assertThat(tagList).contains("일리", "커피캡슐");
    }

    @Test
    @DisplayName("배송형 상품을 수정한다")
    void updateDeliveryProduct() throws ProductNotFoundException {
        // given
        ProductTag pt1 = new ProductTag("일리");
        ProductTag pt2 = new ProductTag("커피캡슐");
        Product product = new Product("일리머신&커피캡슐"
                , 230000
                , 2
                ,"일리배송형 커피머신입니다.");
        product.setProductStatus(ProductStatus.SOLD_OUT.name());
        product.setProductTags(pt1);
        product.setProductTags(pt2);

        int fee = 1000;
        int weight = 10;

        DeliveryProduct delivertProduct = DeliveryProduct.fromProduct(
                product
                , fee
                , weight);

        Product saved = productRepository.save(delivertProduct);

        // 영속성 컨텍스트 초기화 (flush + clear)
        em.flush();
        em.clear();

        // when
        DeliveryProduct deliveryProduct = productRepository.findByDeliveryProductId(saved.getId())
                .orElseThrow(ProductNotFoundException::new);

        deliveryProduct.update(
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
        deliveryProduct.updateProductTags(deliveryProduct, newTags);

        int newFee = 2000;
        int newWeight = 20;

        deliveryProduct.setFee(newFee);
        deliveryProduct.setWeight(newWeight);

        // then
        Product foundEntity = productRepository.findByProductId(saved.getId()).orElseThrow(ProductNotFoundException::new);

        assertThat(foundEntity.getName()).isEqualTo("네스프레소 머신");
        assertThat(foundEntity.getPrice()).isEqualTo(310000);
        assertThat(foundEntity.getStockQuantity()).isEqualTo(1);
        assertThat(foundEntity.getDescription()).isEqualTo("네스프레소 머신입니다");
        assertThat(foundEntity.getProductStatus().toString()).isEqualTo("ON_SALE");
        List<String> tagList = foundEntity.getProductTags().stream().map(ProductTag::getTagName).collect(Collectors.toList());
        assertThat(tagList).contains("네스프레소", "커피머신");
    }

    @Test
    @DisplayName("배송형 상품을 삭제한다")
    void deleteDeliveryProduct() {
        // given
        ProductTag pt1 = new ProductTag("일리");
        ProductTag pt2 = new ProductTag("커피캡슐");
        Product product = new Product("일리머신&커피캡슐"
                , 230000
                , 2
                ,"일리배송형 커피머신입니다.");
        product.setProductStatus(ProductStatus.SOLD_OUT.name());
        product.setProductTags(pt1);
        product.setProductTags(pt2);

        int fee = 1000;
        int weight = 10;

        DeliveryProduct delivertProduct = DeliveryProduct.fromProduct(
                product
                , fee
                , weight);

        Product saved = productRepository.save(delivertProduct);

        // 영속성 컨텍스트 초기화 (flush + clear)
        em.flush();
        em.clear();

        // when
        Product deleteProduct = productRepository.findByProductId(saved.getId()).orElseThrow(ProductNotFoundException::new);
        productRepository.delete(deleteProduct);

        // 영속성 컨텍스트 초기화 (flush + clear)
        em.flush();
        em.clear();

        // then
        assertThatThrownBy(() ->
                productRepository.findByProductId(saved.getId())
                        .orElseThrow(ProductNotFoundException::new)
        )
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("해당 상품정보를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("BackOffice Product 수정(조회)시에는 DB에 Lock을 건다(비관락)")
    void testProductFindLock() throws InterruptedException {
        // given
        Product product = new Product("일리머신&커피캡슐", 230000, 2,"일리배송형 커피머신입니다.");
        product.setProductStatus(ProductStatus.SOLD_OUT.name());
        int fee = 0;
        int weight = 0;
        DeliveryProduct delivertProduct = DeliveryProduct.fromProduct(product, fee, weight);

        Product saved = productRepository.save(delivertProduct);

        // 영속성 컨텍스트 초기화 (flush + clear)
        em.flush();
        em.clear();

        // when
        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> {
            transactionTemplate.executeWithoutResult(status -> {
                productRepository.findByDeliveryProductId(saved.getId())
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
                productRepository.findByDeliveryProductId(saved.getId())
                        .orElseThrow(ProductNotFoundException::new);
            });
        });

        executor.shutdown();

        // then
        assertThatThrownBy(() -> future.get()).hasCauseInstanceOf(PessimisticLockException.class);
    }
}