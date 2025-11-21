package com.example.study.member.command.application;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BackofficeMemberLoginSaveForm {

    @NotBlank
    private String loginId;

    @NotBlank
    private String password;

    public BackofficeMemberLoginSaveForm(){};

    public BackofficeMemberLoginSaveForm(String loginId, String password){
        this.loginId = loginId;
        this.password = password;
    }
}
