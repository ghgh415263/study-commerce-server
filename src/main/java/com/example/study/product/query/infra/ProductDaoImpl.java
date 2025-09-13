package com.example.study.product.query.infra;

import com.example.study.common.CustomPage;
import com.example.study.product.command.domain.product.Product;
import com.example.study.product.command.domain.product.ProductType;
import com.example.study.product.query.dao.ProductDao;
import com.example.study.product.query.dao.ProductQueryDto;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ProductDaoImpl implements ProductDao {

    private final EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public CustomPage<ProductQueryDto> findByProductIdWithPaging(String type, int page, int size) {
        int offset = (page - 1) * size;

        // 1. 상품 타입 조회
        ProductType requestProductType = ProductType.from(type);
        String productType = requestProductType != null ? requestProductType.name() : null;

        // 2. 상품 아이디 조회 + 태그 조회
        List<Long> productIds = em.createQuery(
                    "SELECT p.id " +
                            "FROM Product p " +
                            "WHERE (:productType IS NULL OR p.productType = :productType) " +
                            "ORDER BY p.createdAt DESC",
                    Long.class
                )
                .setParameter("productType", productType)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();

        List<Object[]> content = em.createQuery(
                        "SELECT p, pt.tagName " +
                                "FROM Product p " +
                                "LEFT JOIN p.productTags pt " +
                                "WHERE p.id IN :ids " +
                                "ORDER BY p.createdAt DESC",
                        Object[].class
                )
                .setParameter("ids", productIds)
                .getResultList();

        // 3. 조회 결과 그룹핑
        List<ProductQueryDto> productQueryDtoList = getProductQueryDtos(content);

        // 4. 전체 개수 조회
        Long totalElements = em.createQuery(
                        "SELECT COUNT(p) " +
                                "FROM Product p " +
                                "WHERE (:productType IS NULL OR p.productType = :productType) ",
                        Long.class
                )
                .setParameter("productType", productType)
                .getSingleResult();

        return new CustomPage<>(productQueryDtoList, page, size, totalElements);
    }

    private List<ProductQueryDto> getProductQueryDtos(List<Object[]> content) {
        Map<Long, ProductQueryDto> resultMap = new LinkedHashMap<>();

        for (Object[] row : content) {
            Product p = (Product) row[0];
            String tagName = (String) row[1];

            ProductQueryDto dto = resultMap.computeIfAbsent(
                    p.getId(),
                    id -> new ProductQueryDto(
                            p.getId(),
                            p.getName(),
                            p.getPrice(),
                            p.getStockQuantity(),
                            p.getDescription(),
                            p.getProductStatus()
                    )
            );

            if (tagName != null) {
                dto.addTag(tagName);
            }
        }
        return new ArrayList<>(resultMap.values());
    }
}