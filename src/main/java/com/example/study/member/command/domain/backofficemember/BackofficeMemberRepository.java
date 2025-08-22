package com.example.study.member.command.domain.backofficemember;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BackofficeMemberRepository extends JpaRepository<BackofficeMember, UUID> {

    Optional<BackofficeMember> findByLoginId(String loginId);
}
