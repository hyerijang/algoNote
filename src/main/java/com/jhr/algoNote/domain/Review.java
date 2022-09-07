package com.jhr.algoNote.domain;

import static javax.persistence.FetchType.LAZY;

import com.jhr.algoNote.domain.content.ReviewContent;
import com.jhr.algoNote.domain.tag.ReviewTag;
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
import javax.validation.constraints.NotEmpty;
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
public class Review extends BaseTimeEntity {


    @Id
    @GeneratedValue
    @Column(name = "review_id", updatable = false)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotEmpty
    private String title;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "review_content_id")
    private ReviewContent content;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewTag> reviewTags = new ArrayList<>();

    // == 빌더 ==//
    @Builder
    public static Review createReview(@NonNull Member member, @NonNull String title,
        @NonNull Problem problem, @NonNull ReviewContent content, List<ReviewTag> reviewTagList) {
        Review review = new Review();
        review.setMember(member);
        review.title = title;
        //연관관계설정
        review.addProblem(problem);
        review.setContent(content);
        // 리뷰 태그 추가
        review.renewalReviewTag(reviewTagList);
        return review;
    }

    //== 연관관계 메서드 == //
    public void setMember(Member member) {
        this.member = member;
        member.getReviews().add(this);
    }

    public void addProblem(Problem problem) {
        this.problem = problem;
        problem.getReviews().add(this);
    }

    public void addReviewTag(ReviewTag reviewTag) {
        this.reviewTags.add(reviewTag);
        reviewTag.setReview(this);
    }

    public void setContent(ReviewContent content) {
        this.content = content;
        content.setReview(this);
    }

    //== 비즈니스 로직==//
    public void patch(String title, String contentText) {
        if (title != null) {
            this.title = title;
        }
        if (contentText != null) {
            this.content.updateText(contentText);
        }
    }

    public void renewalReviewTag(List<ReviewTag> reviewTags) {
        for (ReviewTag rt : reviewTags) {
            this.addReviewTag(rt);
        }
    }
}
