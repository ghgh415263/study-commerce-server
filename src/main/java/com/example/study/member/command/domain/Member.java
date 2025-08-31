package com.example.study.member.command.domain;

import com.example.study.order.command.domain.AddressVO;
import com.example.study.common.persistance.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.util.UUID;

@Audited
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Embedded
    private AddressVO memberAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    @Version
    private Integer version;

    public Member(String loginId, String password, String email, String name, AddressVO memberAddress) {
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.name = name;
        this.memberAddress = memberAddress;
    }

    public void withdraw() {
        if (this.status == MemberStatus.WITHDRAWN || this.status == MemberStatus.SUSPENDED) {
            throw new InvalidMemberStateException(this.status, MemberStatus.WITHDRAWN);
        }

        this.status = MemberStatus.WITHDRAWN;
    }

    public void suspend() {
        if (this.status != MemberStatus.ACTIVE) {
            throw new InvalidMemberStateException(this.status, MemberStatus.SUSPENDED);
        }
        this.status = MemberStatus.SUSPENDED;
    }

    public void activate() {
        if (this.status == MemberStatus.WITHDRAWN) {
            throw new InvalidMemberStateException(this.status, MemberStatus.ACTIVE);
        }
        this.status = MemberStatus.ACTIVE;
    }

    public void changePassword(String newPassword) {
        if (this.status != MemberStatus.ACTIVE) {
            throw new InvalidMemberStateException(this.status, MemberStatus.ACTIVE);
        }
        this.password = newPassword;
    }

    public void updateInfo(String name, String email, AddressVO address) {
        if (this.status == MemberStatus.WITHDRAWN || this.status == MemberStatus.SUSPENDED) {
            throw new InvalidMemberStateException(this.status, MemberStatus.ACTIVE);
        }
        this.name = name;
        this.email = email;
        this.memberAddress = address;
    }
}
