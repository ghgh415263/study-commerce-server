package com.example.study.member.command.application;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberUpdateDto(

        @NotBlank(message = "이름은 필수 입력값입니다.")
        String name,

        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "주소는 필수 입력값입니다.")
        String baseAddress,

        @NotBlank(message = "상세 주소는 필수 입력값입니다.")
        String detailAddress,

        @NotBlank(message = "우편번호는 필수 입력값입니다.")
        String zipcode,

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        String password

) { }
