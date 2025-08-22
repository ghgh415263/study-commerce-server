package com.example.study.common.persistance;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Audited
@Getter
@MappedSuperclass // 상속받는 엔티티가 이 필드들을 컬럼으로 인식하게 함
@EntityListeners(AuditingEntityListener.class) // JPA Auditing 기능 활성화
public abstract class BaseUpdateEntity extends BaseEntity {

    /**
     * 마지막 수정자 ID
     * AuditorAware<String>을 통해 자동 주입됨
     */
    @LastModifiedBy
    @Column(nullable = false)
    private String modifiedBy;

    /**
     * 마지막 수정 시간
     * 엔티티가 수정될 때 자동으로 현재 시간으로 갱신됨
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt;
}
