package com.example.study.common.authentication.fo;

import com.example.study.common.persistance.BaseEntity;
import com.example.study.common.util.AuthenticationUtils;
import com.example.study.member.command.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.time.Instant;
import java.time.LocalDateTime;

@Audited
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtBlacklist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String jwtHashId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Instant  expiredAt;

    @Column(nullable = false)
    private LocalDateTime logoutAt;

    public JwtBlacklist(String jwt, Long memberId, Instant expiredAt, LocalDateTime logoutAt){
        this.jwtHashId = AuthenticationUtils.hashJwtWithSHA256(jwt);
        this.memberId = memberId;
        this.expiredAt = expiredAt;
        this.logoutAt = logoutAt;
    }
}
