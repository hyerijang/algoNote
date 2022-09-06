package com.jhr.algoNote.dto;

import javax.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class CreateMemberRequest {

    private String name;
    @NotEmpty(message = "회원 이메일은 필수입니다")
    private String email;
    private String picture;
}
