package com.jhr.algoNote.api.dto;

import javax.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(AccessLevel.NONE)
public class CreateMemberRequest {

    private String name;
    @NotEmpty(message = "회원 이메일은 필수입니다")
    private String email;
    private String picture;
}
