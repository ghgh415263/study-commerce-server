package com.example.study.member.command.application;

import com.example.study.member.command.domain.*;
import com.example.study.order.order.command.domain.AddressVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 회원 가입 서비스를 담당하는 클래스입니다.
 */
@RequiredArgsConstructor
@Service
public class MemberJoinService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final PasswordMeter passwordMeter;

    private final MemberCheckService memberCheckService;

    private final OrderWalletClient orderWalletClient;

    /**
     * 회원 가입 처리 메서드
     * 1. 비밀번호 강도 확인 (약한 경우 예외 발생)
     * 2. 로그인 ID 중복 검사 (중복 시 예외 발생)
     * 3. Member 객체 생성 후 저장
     *
     * @param memberJoinDto 가입 요청 데이터 (회원 정보와 주소 정보 포함)
     * @return 저장된 회원의 ID
     */
    @Transactional
    public Long saveMember(MemberJoinDto memberJoinDto) {

        if (passwordMeter.isWeak(memberJoinDto.password()))
            throw new WeakPasswordException();

        if (memberCheckService.isDuplicateUsername(memberJoinDto.loginId()))
            throw new DuplicatedLoginIdException();

        AddressDto addressDto = memberJoinDto.address();
        AddressVO address = new AddressVO(
                addressDto.zipCode(),
                addressDto.baseAddress(),
                addressDto.detailAddress());

        Member member = new Member(
                memberJoinDto.loginId(),
                passwordEncoder.hashPassword(memberJoinDto.password()),
                memberJoinDto.email(),
                memberJoinDto.name(),
                address);

        memberRepository.save(member);

        orderWalletClient.createWallet(member.getId());

        return member.getId();
    }
}