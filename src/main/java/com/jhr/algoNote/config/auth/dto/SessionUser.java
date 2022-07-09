package com.jhr.algoNote.config.auth.dto;

import com.jhr.algoNote.domain.Member;
import java.io.Serializable;
import lombok.Getter;

/**
 * 직렬화 기능을 가진 User클래스
 */
@Getter
public class SessionUser implements Serializable {

    private String name;
    private String email;
    private String picture;

    public SessionUser(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();
        this.picture = member.getPicture();
    }
}