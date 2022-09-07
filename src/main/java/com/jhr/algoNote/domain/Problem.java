package com.jhr.algoNote.domain;

import static javax.persistence.FetchType.LAZY;

import com.jhr.algoNote.domain.content.ProblemContent;
import com.jhr.algoNote.domain.tag.ProblemTag;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Problem extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "problem_id", updatable = false)
    private Long id;

    @NotNull
    private String title;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @Enumerated(EnumType.STRING)
    private Site site;
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
        String site, String url, List<ProblemTag> problemTagList, Long id) {
        Problem problem = new Problem();
        problem.id = id;
        problem.title = title;
        problem.url = url;
        problem.site = site == null ? Site.NO : Site.valueOf(site);
        problem.setMember(member);
        problem.setContent(content);
        problem.renewalProblemTag(problemTagList);
        return problem;
    }

    //== 비즈니스 로직 ==//


    public void patch(String title, String contentText,  String site, String url) {
        if (!isEmptyOrNull(title)) {
            this.title = title;
        }
        if (!isEmptyOrNull(contentText))
        {
            this.getContent().editText(contentText);
        }
        if (!isEmptyOrNull(site)) {
            this.site = Site.valueOf(site);
        }
        if (!isEmptyOrNull(url)) {
            this.url = url;
        }

    }

    private static boolean isEmptyOrNull(String str) {
        if (str == null) {
            return true;
        }
        return false;
    }


    /**
     * problemTag를 비우고 새로운 ProblemTag들 추가
     *
     * @param problemTagList 추가할 ProblemTag의 List
     */
    public void renewalProblemTag(List<ProblemTag> problemTagList) {
        if (problemTagList == null) {
            return;
        }

        for (ProblemTag problemTag : problemTagList) {
            this.addProblemTag(problemTag);
        }
    }

}
