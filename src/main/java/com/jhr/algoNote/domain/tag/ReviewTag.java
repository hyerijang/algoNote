package com.jhr.algoNote.domain.tag;

import static javax.persistence.FetchType.LAZY;

import com.jhr.algoNote.domain.Review;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ReviewTag {

    @Id
    @GeneratedValue
    @Column(name = "review_tag_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    // ==생성 메서드==
    public static ReviewTag createProblemTag(Tag tag) {
        ReviewTag rt = new ReviewTag();
        rt.setTag(tag);
        return rt;
    }
}
