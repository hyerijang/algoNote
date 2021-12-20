package com.jhr.algoNote.domain.content;

import static javax.persistence.FetchType.LAZY;

import com.jhr.algoNote.domain.Problem;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ProblemContent {

    @Id
    @GeneratedValue
    @Column(name = "problem_content_id")
    private Long id;
    private String text;


    @OneToOne(fetch = LAZY, mappedBy = "content")
    private Problem problem;


    //== 생성 메서드 ==///
    public static ProblemContent createProblemContent(String text) {
        ProblemContent pc = new ProblemContent();
        pc.text = text;
        return pc;
    }

}
