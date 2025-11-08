package com.example.study.member.command.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, RevisionRepository<Member, Long, Integer> {

    Optional<Member> findByLoginId(String loginId);
}
