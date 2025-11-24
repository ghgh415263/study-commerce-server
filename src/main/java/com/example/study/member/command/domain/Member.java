package com.example.study.member.command.domain;

import com.example.study.order.order.command.domain.AddressVO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@Audited
@Entity
@Getter
@DiscriminatorValue("MEMBER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends MemberBase {

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Embedded
    private AddressVO memberAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status = MemberStatus.ACTIVE;

    public Member(String loginId, String password, String email, String name, AddressVO memberAddress) {
        super(loginId, password);
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
        super.password = newPassword;
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
