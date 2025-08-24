package com.example.study.learningtest;

import com.example.study.integration.TestPersistenceAuditorConfig;
import com.example.study.order.command.application.DeliveryAddressNotFoundException;
import com.example.study.product.command.domain.Product;
import com.example.study.product.command.domain.ProductRepository;
import com.example.study.product.command.domain.ProductTag;
import com.example.study.product.command.infra.JpaProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Import(TestPersistenceAuditorConfig.class)
@DataJpaTest
public class ProductTest {

    @Autowired
    private EntityManager em;

    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = new JpaProductRepository(em);
    }

    @Test
    @DisplayName("Product를 저장한다")
    void saveProduct() {
        // given
        ProductTag pt1 = new ProductTag("핸드폰");
        ProductTag pt2 = new ProductTag("삼성");
        ProductTag pt3 = new ProductTag("플래그쉽");
        Product product = new Product("갤럭시S25", 1520000, 3,"갤럭시");
        product.setProductTags(pt1);
        product.setProductTags(pt2);
        product.setProductTags(pt3);

        // when
        Product saved = productRepository.save(product);

        // 영속성 컨텍스트 초기화 (flush + clear)
        em.flush();
        em.clear();

        // then
        Product foundEntity = productRepository.findByProductId(saved.getId()).orElseThrow(DeliveryAddressNotFoundException::new);

        assertThat(foundEntity.getName()).isEqualTo("갤럭시S25");
        assertThat(foundEntity.getPrice()).isEqualTo(1520000);
        assertThat(foundEntity.getStockQuantity()).isEqualTo(3);
        List<String> tagList = foundEntity.getProductTags().stream().map(ProductTag::getTagName).collect(Collectors.toList());
        assertThat(tagList).contains("핸드폰", "삼성", "플래그쉽");
    }
}
