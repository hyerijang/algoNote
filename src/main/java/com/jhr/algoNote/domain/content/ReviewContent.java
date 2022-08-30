package com.jhr.algoNote.domain.content;

import static javax.persistence.FetchType.LAZY;

import com.jhr.algoNote.domain.Review;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class ReviewContent {

    @Id
    @GeneratedValue
    @Column(name = "review_content_id")
    private Long id;

    @Lob
    @NotNull
    private String text;

    @OneToOne(fetch = LAZY, mappedBy = "content")
    private Review review;

    public static ReviewContent of(String text) {
        return ReviewContent.builder().text(text).build();
    }
    public void updateText(String text) {
        this.text = text;
    }
    public void setReview(Review review){
        this.review = review;
    }

}
