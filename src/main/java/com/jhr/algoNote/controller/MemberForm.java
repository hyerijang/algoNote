package com.jhr.algoNote.controller;

import com.jhr.algoNote.domain.Role;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberForm {

    @NotEmpty(message = "회원 이름은 필수입니다")
    private String name;
    @NotEmpty(message = "회원 이메일은 필수입니다")
    private String email;
    private String picture;
    private Role role;
}
