package com.jhr.algoNote.domain.tag;

import java.util.regex.Pattern;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
public class Tag {

    @Id
    @GeneratedValue
    @Column(name = "tag_id")
    Long id;

    @Column(unique = true)
    String name;

    @Builder
    public Tag(String name) {

        //태그 이름에 공백 혹은 특수문자가 포함된 경우
        String pattern = "^[0-9|a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣]*$";
        if (!Pattern.matches(pattern, name)) {
            throw new IllegalArgumentException("태그 이름으로 공백 혹은 특수문자가 입력되었습니다.");
        }

        this.name = name;
    }
}
