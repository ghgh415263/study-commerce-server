package com.example.study.member.command.domain.backofficemember;

import com.example.study.member.command.domain.MemberBase;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@Audited
@Entity
@Getter
@DiscriminatorValue("BACKOFFICE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BackofficeMember extends MemberBase {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BackofficeRole role;

    public BackofficeMember(String loginId, String password, BackofficeRole role) {
        super(loginId, password);
        this.role = role;
    }

    public void changeRole(BackofficeRole newRole) {
        this.role = newRole;
    }
}