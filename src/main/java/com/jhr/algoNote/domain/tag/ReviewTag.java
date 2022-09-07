package com.jhr.algoNote.domain.tag;

import static javax.persistence.FetchType.LAZY;

import com.jhr.algoNote.domain.Review;
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
import lombok.Setter;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class ReviewTag {

    @Id
    @GeneratedValue
    @Column(name = "review_tag_id", updatable = false)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    // ==생성 메서드==
    public static ReviewTag createReviewTag(Tag tag) {
        ReviewTag rt = new ReviewTag();
        rt.setTag(tag);
        return rt;
    }
}
