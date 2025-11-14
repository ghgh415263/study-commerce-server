package com.example.study.member.command.application;

import com.example.study.member.command.domain.Member;
import com.example.study.member.command.domain.MemberRepository;
import com.example.study.member.command.domain.PasswordEncoder;
import com.example.study.order.order.command.domain.AddressVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberUpdateService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void updateMember(Long memberId, MemberUpdateDto dto) {

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .filter(m -> passwordEncoder.isMatch(dto.password(), m.getPassword()))
                .orElseThrow(InvalidCredentialsException::new);

        // 업데이트
        member.updateInfo(dto.name(), dto.email(), new AddressVO(dto.zipcode(), dto.detailAddress(), dto.baseAddress()));
    }
}
