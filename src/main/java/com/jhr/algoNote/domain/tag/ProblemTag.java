package com.jhr.algoNote.domain.tag;

import static javax.persistence.FetchType.LAZY;

import com.jhr.algoNote.domain.Problem;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProblemTag {

    @Id
    @GeneratedValue
    @Column(name = "problem_tag_id", updatable = false)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    private void setTag(Tag tag) {
        this.tag = tag;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    // == 생성 메서드 ==//
    public static ProblemTag createProblemTag(Tag tag) {
        ProblemTag pt = new ProblemTag();
        pt.setTag(tag);
        return pt;
    }


}
