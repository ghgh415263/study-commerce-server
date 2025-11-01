package com.example.study.member.command.domain.backofficemember;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BackofficeMemberRepository extends JpaRepository<BackofficeMember, Long> {

    Optional<BackofficeMember> findByLoginId(String loginId);
}
