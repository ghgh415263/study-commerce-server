package com.example.study.common.persistance;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Audited
@Getter
@MappedSuperclass  // 이 클래스를 상속한 엔티티가 필드를 컬럼으로 인식하게 함
@EntityListeners(AuditingEntityListener.class) // JPA Auditing 기능을 활성화
public abstract class BaseEntity {

    /**
     * 생성한 사용자 ID
     * AuditorAware<T>를 통해 자동 주입됨
     */
    @CreatedBy
    @Column(updatable = false, nullable = false)
    private String createdBy;

    /**
     * 생성된 시간
     * 자동으로 현재 시간으로 설정됨
     */
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
}
