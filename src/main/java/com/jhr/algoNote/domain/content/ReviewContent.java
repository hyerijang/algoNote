package com.jhr.algoNote.domain.content;

import static javax.persistence.FetchType.LAZY;

import com.jhr.algoNote.domain.Review;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ReviewContent {

    @Id
    @GeneratedValue
    @Column(name = "review_content_id")
    private Long id;

    @NotNull
    private String text;


    @OneToOne(fetch = LAZY, mappedBy = "content")
    private Review review;


}
