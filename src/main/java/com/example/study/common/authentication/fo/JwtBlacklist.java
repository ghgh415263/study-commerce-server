package com.example.study.common.authentication.fo;

import com.example.study.common.persistance.BaseUpdateEntity;
import com.example.study.common.util.AuthenticationUtils;
import com.example.study.member.command.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.Date;

@Audited
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtBlacklist extends BaseUpdateEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String jwtHashId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private Date expiredAt;

    @Column(nullable = false)
    private LocalDateTime logoutAt;

    public JwtBlacklist(String jwt, Member member, Date expiredAt, LocalDateTime logoutAt){
        this.jwtHashId = AuthenticationUtils.hashJwtWithSHA256(jwt);
        this.member = member;
        this.expiredAt = expiredAt;
        this.logoutAt = logoutAt;
    }
}
