package com.example.study.unit.help;

import com.example.study.member.command.domain.Member;
import com.example.study.member.command.domain.MemberStatus;

import java.lang.reflect.Field;

public class MemberTestFactory {

    public static Member createActiveMember() {
        return createMemberWithStatus(MemberStatus.ACTIVE);
    }

    public static Member createSuspendedMember() {
        return createMemberWithStatus(MemberStatus.SUSPENDED);
    }

    public static Member createWithdrawnMember() {
        return createMemberWithStatus(MemberStatus.WITHDRAWN);
    }

    public static Member createMemberWithStatus(MemberStatus status) {
        Member member = new Member(
                "testLoginId",
                "testPassword",
                "test@example.com",
                "테스트회원",
                null // 또는 테스트용 AddressVO
        );

        setStatus(member, status);
        return member;
    }

    public static Member createMember(String loginId, String password, Long id) {
        Member member = new Member(
                loginId,
                password,
                loginId + "@example.com", // 테스트용 이메일
                "테스트회원",
                null
        );
        setId(member, id);
        return member;
    }

    private static void setId(Object target, Long id) {
        try {
            Class<?> clazz = target.getClass();

            while (clazz != null) {
                try {
                    Field field = clazz.getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(target, id);
                    return;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }

            throw new NoSuchFieldException("'id' 필드를 찾을 수 없습니다.");
        } catch (Exception e) {
            throw new RuntimeException("회원 id 설정에 실패했습니다.", e);
        }
    }

    private static void setStatus(Member member, MemberStatus status) {
        try {
            Field field = Member.class.getDeclaredField("status");
            field.setAccessible(true);
            field.set(member, status);
        } catch (Exception e) {
            throw new RuntimeException("회원 상태 설정에 실패했습니다.", e);
        }
    }
}
