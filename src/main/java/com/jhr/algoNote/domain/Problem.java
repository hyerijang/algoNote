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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
@Entity
public class Problem extends BaseTimeEntity {

    @Id
    @GeneratedValue
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
    private void setMember(Member member) {
        this.member = member;
        member.getProblems().add(this);
    }

    private void addProblemTag(ProblemTag problemTag) {
        this.problemTags.add(problemTag);
        problemTag.setProblem(this);
    }

    private void addReview(Review review) {
        reviews.add(review);
        review.setProblem(this);
    }

    private void setContent(ProblemContent content) {
        this.content = content;
        content.setProblem(this);
    }

    private void setTitle(String title) {
        this.title = title;
    }

    //== 생성 메서드 ==//

    /**
     * 문제 생성 후 제목, 내용 및 태그 등록
     *
     * @param member
     * @param content
     * @param tags
     * @return
     */
    public static Problem createProblem(Member member, String title, ProblemContent content,
        ProblemTag... tags
    ) {
        Problem problem = new Problem();
        problem.setMember(member);
        problem.setTitle(title);
        problem.setContent(content);
        //태그 추가
        for (ProblemTag tag : tags) {
            problem.addProblemTag(tag);
        }
        return problem;
    }


}
