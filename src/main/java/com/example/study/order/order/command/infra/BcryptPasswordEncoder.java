package com.example.study.order.order.command.infra;

import com.example.study.member.command.domain.PasswordEncoder;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

/**
 * BCrypt 알고리즘을 사용하여 비밀번호를 해시하고 검증하는 PasswordEncoder 구현체입니다.
 */
@Component
public class BcryptPasswordEncoder implements PasswordEncoder {

    /**
     * 평문 비밀번호를 BCrypt 방식으로 해시합니다.
     *
     * @param plaintext 평문 비밀번호
     * @return 해시된 비밀번호 문자열
     */
    @Override
    public String hashPassword(String plaintext) {
        return BCrypt.hashpw(plaintext, BCrypt.gensalt());
    }

    /**
     * 평문 비밀번호와 해시된 비밀번호가 일치하는지 검사합니다.
     *
     * @param plaintext 평문 비밀번호
     * @param hashed    저장된 해시 비밀번호
     * @return 일치하면 {@code true}, 아니면 {@code false}
     */
    @Override
    public boolean isMatch(String plaintext, String hashed) {
        return BCrypt.checkpw(plaintext, hashed);
    }
}
