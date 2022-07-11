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
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Review extends BaseTimeEntity {


    @Id
    @GeneratedValue
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "review_content_id")
    private ReviewContent content;

    @OneToMany(mappedBy = "review")
    private List<ReviewTag> reviewTags = new ArrayList<>();


    // == 빌더 ==//
    @Builder
    public static Review createReview(@NonNull Member member, @NonNull String title,
        @NonNull Problem problem,@NonNull ReviewContent content, List<ReviewTag> reviewTagList) {
        Review review = new Review();
        review.member = member;
        review.title = title;
        review.problem = problem;
        review.content = content;
        // 태그 추가
        for (ReviewTag rt : reviewTagList) {
            review.reviewTags.add(rt);
        }

        return review;
    }

    //== 연관관계 메서드 == //
    public void setMember(Member member) {
        this.member = member;
        member.getReviews().add(this);
    }

    public void addReviewTag(ReviewTag reviewTag) {
        this.reviewTags.add(reviewTag);
        reviewTag.setReview(this);
    }

    public void setContent(ReviewContent content) {
        this.content = content;
        content.setReview(this);
    }

}
