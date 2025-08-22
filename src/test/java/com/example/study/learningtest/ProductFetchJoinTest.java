package com.example.study.learningtest;

import com.example.study.integration.TestPersistenceAuditorConfig;
import com.example.study.product.command.domain.Product;
import com.example.study.product.command.domain.ProductTag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceUtil;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestPersistenceAuditorConfig.class)
@DataJpaTest
public class ProductFetchJoinTest {

    @Autowired
    private EntityManager em;

    @Test
    void fetchJoinProductWithTags() {
        // given
        Product product = new Product("Mouse", 15000, 30);
        ProductTag tag1 = new ProductTag("Electronics");
        ProductTag tag2 = new ProductTag("Accessory");

        product.setProductTags(tag1);
        product.setProductTags(tag2);

        em.persist(product); // cascade 로 ProductTag도 저장됨
        em.flush();
        em.clear(); // 1차 캐시 비우기

        // when
        String jpql = "SELECT DISTINCT p FROM Product p " +
                "JOIN FETCH p.productTags " +
                "WHERE p.id = :id";

        Product fetchedProduct = em.createQuery(jpql, Product.class)
                .setParameter("id", product.getId())
                .getSingleResult();

        // then
        assertEquals("Mouse", fetchedProduct.getName());
        assertEquals(2, fetchedProduct.getProductTags().size());
    }

    @Test
    void fetchJoinWithoutDistinctTest() {
        // given
        Product product = new Product("Mouse", 15000, 30);
        ProductTag tag1 = new ProductTag("Electronics");
        ProductTag tag2 = new ProductTag("Accessory");

        product.setProductTags(tag1);
        product.setProductTags(tag2);

        em.persist(product); // cascade 설정으로 태그도 저장됨
        em.flush();
        em.clear(); // 1차 캐시 비우기

        // when
        String jpql = "SELECT p FROM Product p " +
                "JOIN FETCH p.productTags " +
                "WHERE p.id = :id";

        Product fetchedProduct = em.createQuery(jpql, Product.class)
                .setParameter("id", product.getId())
                .getSingleResult();

        // then
        assertEquals("Mouse", fetchedProduct.getName());
    }

    @Test
    void joinSelectProductAndTag() {
        // given
        Product product = new Product("Monitor", 30000, 50);
        ProductTag tag1 = new ProductTag("Display");
        ProductTag tag2 = new ProductTag("Electronics");

        product.setProductTags(tag1);
        product.setProductTags(tag2);

        em.persist(product);
        em.flush();
        em.clear();

        // when
        String jpql = "SELECT p, t FROM Product p JOIN p.productTags t WHERE t.tagName = :tagName";

        List<Object[]> results = em.createQuery(jpql, Object[].class)
                .setParameter("tagName", "Display")
                .getResultList();

        // then
        for (Object[] row : results) {
            System.out.println("컬렉션 타입 처음 확인: " + product.getProductTags().getClass().getName());
            Product p = (Product) row[0];
            ProductTag t = (ProductTag) row[1];

            System.out.println("컬렉션 타입 처음 확인2: " + product.getProductTags().getClass().getName());
            PersistenceUtil util = Persistence.getPersistenceUtil();
            boolean isInitialized = util.isLoaded(p, "productTags");  // 무조건 프록시군요.

            System.out.println("Product: " + p.getName());
            System.out.println("Tag: " + t.getTagName());
            p.getProductTags().stream().map(tt -> tt).collect(Collectors.toSet());
            System.out.println("컬렉션 타입 처음 확인3: " + product.getProductTags().getClass().getName());
        }

        assertEquals(1, results.size());
    }


}
