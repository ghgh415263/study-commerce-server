package com.example.study.member.command.domain;

import com.example.study.common.persistance.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@Audited
@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED) // JOINED 전략
@DiscriminatorColumn(
        name = "member_type",
        length = 30
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class MemberBase extends BaseUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    protected String loginId;

    @Column(nullable = false)
    protected String password;

    @Version
    protected Integer version;

    protected MemberBase(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }
}
