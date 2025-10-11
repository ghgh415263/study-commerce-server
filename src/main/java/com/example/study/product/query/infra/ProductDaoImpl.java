package com.example.study.product.query.infra;

import com.example.study.common.CustomPage;
import com.example.study.product.command.domain.product.ProductStatus;
import com.example.study.product.query.dao.BOProductQueryDto;
import com.example.study.product.query.dao.FOProductQueryDto;
import com.example.study.product.query.dao.ProductDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductDaoImpl implements ProductDao {

    private final EntityManager em;

    /**
     * FO 상품 조회
     */
    @Override
    @Transactional(readOnly = true)
    public CustomPage<FOProductQueryDto> findByFOProductIdWithPaging(int page, int size) {
        int offset = (page - 1) * size;

        // 상품 조회 (DTO 직접 매핑)
        List<FOProductQueryDto> fOProductQueryDtos = em.createQuery(
                        "SELECT new com.example.study.product.query.dao.FOProductQueryDto(" +
                                "p.id, p.name, p.price, p.stockQuantity, " +
                                "p.description, p.productStatus, TYPE(p)) " +
                                "FROM Product p " +
                                "ORDER BY p.createdAt DESC",
                        FOProductQueryDto.class)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();

        // 상품 id 추출, 태그 리스트 조회
        List<Long> productIds = fOProductQueryDtos.stream()
                .map(FOProductQueryDto::getId)
                .toList();

        @SuppressWarnings("unchecked")
        List<Tuple> tagList = em.createNativeQuery(
                        "SELECT pt.product_id AS productId, " +
                                "pt.tag_name AS tagName " +
                                "FROM product_tag pt " +
                                "WHERE pt.product_id IN :ids",
                        Tuple.class)
                .setParameter("ids", productIds)
                .getResultList();

        // 상품 queryDtos + 태그 조립
        Map<Long, FOProductQueryDto> dtoMap = fOProductQueryDtos.stream()
                .collect(Collectors.toMap(FOProductQueryDto::getId, dto -> dto));

        tagList.forEach(t -> {
            Long productId = ((Number) t.get("productId")).longValue();
            String tagName = t.get("tagName", String.class);

            FOProductQueryDto dto = dtoMap.get(productId);
            if (dto != null && tagName != null) {
                dto.addTag(tagName);
            }
        });

        // 전체 개수 조회
        Long totalElements = em.createQuery(
                        "SELECT COUNT(p) " +
                                "FROM Product p ",
                        Long.class
                )
                .getSingleResult();

        return new CustomPage<>(fOProductQueryDtos, page, size, totalElements);
    }


    /**
     * BO 상품 조회
     */
    @Override
    @Transactional(readOnly = true)
    public CustomPage<BOProductQueryDto> findByBOProductIdWithPaging(int page, int size) {
        int offset = (page - 1) * size;

        // 상품 조회
        List<BOProductQueryDto> bOProductQueryDtos = em.createQuery(
                        "SELECT new com.example.study.product.query.dao.BOProductQueryDto(" +
                                "p.id, p.name, p.price, p.stockQuantity, " +
                                "p.description, p.productStatus, TYPE(p)) " +
                                "FROM Product p " +
                                "ORDER BY p.createdAt DESC",
                        BOProductQueryDto.class)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();

        // 상품 id 추출, 태그 리스트 조회
        List<Long> productIds = bOProductQueryDtos.stream()
                .map(BOProductQueryDto::getId)
                .toList();

        @SuppressWarnings("unchecked")
        List<Tuple> tagList = em.createNativeQuery(
                        "SELECT pt.product_id AS productId, " +
                                "pt.tag_name AS tagName " +
                                "FROM product_tag pt " +
                                "WHERE pt.product_id IN :ids",
                        Tuple.class)
                .setParameter("ids", productIds)
                .getResultList();

        // 상품 queryDtos + 태그 조립
        Map<Long, BOProductQueryDto> dtoMap = bOProductQueryDtos.stream()
                .collect(Collectors.toMap(BOProductQueryDto::getId, dto -> dto));

        tagList.forEach(t -> {
            Long productId = ((Number) t.get("productId")).longValue();
            String tagName = t.get("tagName", String.class);

            BOProductQueryDto dto = dtoMap.get(productId);
            if (dto != null && tagName != null) {
                dto.addTag(tagName);
            }
        });

        // 전체 개수 조회
        Long totalElements = em.createQuery(
                        "SELECT COUNT(p) " +
                                "FROM Product p ",
                        Long.class
                )
                .getSingleResult();

        return new CustomPage<>(bOProductQueryDtos, page, size, totalElements);
    }
}