package com.jhr.algoNote.domain.content;

import static javax.persistence.FetchType.LAZY;

import com.jhr.algoNote.domain.Problem;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;


@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class ProblemContent {

    @Id
    @GeneratedValue
    @Column(name = "problem_content_id")
    private Long id;

    @Lob
    @NotNull
    private String text;


    @OneToOne(fetch = LAZY, mappedBy = "content")
    private Problem problem;


    //== 생성 메서드 ==///
    public static ProblemContent createProblemContent(@NonNull String text) {
        ProblemContent pc = new ProblemContent();
        pc.text = text;
        return pc;
    }

    public void editText(String contentText) {
        this.text = contentText;
    }
}
