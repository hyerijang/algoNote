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
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Problem extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "problem_id")
    private Long id;

    @NotNull
    private String title;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String site;
    private String url;


    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL)
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

    //== 생성 메서드 ==//


    /**
     * 문제 생성
     */
    @Builder
    public static Problem createProblem(@NonNull Member member, @NonNull String title,
        @NonNull ProblemContent content,
        String site, String url, List<ProblemTag> problemTagList) {

        Problem problem = new Problem();
        //필수 요소 추가
        problem.setMember(member);
        problem.title = title;
        problem.setContent(content);

        //비 필수 요소 추가
        System.out.println("tags.size() = " + problemTagList.size());
        for (ProblemTag problemTag : problemTagList) {
            problem.addProblemTag(problemTag);
            System.out.println("문제와 태그를 연결합니다." + problemTag + " " + problemTag.getProblem());
        }
        problem.url = url;
        problem.site = site;
        return problem;
    }

    public void update(String title, String site, String url, List<ProblemTag> problemTags) {
        this.title = title;
        this.site = site;
        this.url = url;
        this.problemTags = problemTags;
    }
}
