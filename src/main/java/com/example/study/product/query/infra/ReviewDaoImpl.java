package com.example.study.product.query.infra;

import com.example.study.common.CustomPage;
import com.example.study.member.command.domain.MemberStatus;
import com.example.study.product.query.dao.ReviewQueryDto;
import com.example.study.product.query.dao.ReviewDao;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewDaoImpl implements ReviewDao {

    private final EntityManager em;

    @Override
    @Transactional(readOnly = true)
    public CustomPage<ReviewQueryDto> findByProductIdWithPaging(Long productId, int page, int size) {
        int offset = (page - 1) * size; // 1-based page

        // 1. DTO 리스트 조회
        List<ReviewQueryDto> content = em.createQuery(
                        "SELECT new com.example.study.product.query.dao.ReviewQueryDto(r.id, m.loginId, r.memberId, r.content, r.star) " +
                                "FROM Review r JOIN Member m ON r.memberId = m.id " +
                                "WHERE r.productId = :productId " +
                                "AND m.status != :status " +
                                "ORDER BY r.id DESC",
                        ReviewQueryDto.class
                )
                .setParameter("productId", productId)
                .setParameter("status", MemberStatus.SUSPENDED)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();

        // 2. 전체 개수 조회
        Long totalElements = em.createQuery(
                        "SELECT COUNT(r) FROM Review r WHERE r.productId = :productId",
                        Long.class
                )
                .setParameter("productId", productId)
                .getSingleResult();

        // 3. CustomPage 생성 및 반환
        return new CustomPage<>(content, page, size, totalElements);
    }
}
