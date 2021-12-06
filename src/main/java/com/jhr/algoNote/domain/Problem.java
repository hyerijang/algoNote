package com.jhr.algoNote.domain;

import static javax.persistence.FetchType.LAZY;

import com.jhr.algoNote.domain.content.ProblemContent;
import com.jhr.algoNote.domain.tag.ProblemTag;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class Problem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "problem_id")
    private Long id;

    private String title;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String siteName;
    private String url;


    @OneToMany(mappedBy = "problem")
    private List<ProblemTag> problemTags = new ArrayList<>();

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "problem_content_id")
    private ProblemContent content;

    //== 연관관계 메서드 == //
    public void setMember(Member member) {
        this.member = member;
        member.getProblems().add(this);
    }

    public void addProblemTag(ProblemTag problemTag) {
        this.problemTags.add(problemTag);
        problemTag.setProblem(this);
    }

    public void addReview(Review review) {
        reviews.add(review);
        review.setProblem(this);
    }

    public void setContent(ProblemContent content) {
        this.content = content;
        content.setProblem(this);
    }

}
