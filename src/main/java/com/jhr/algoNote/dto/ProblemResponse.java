package com.jhr.algoNote.dto;

import static java.util.stream.Collectors.toList;

import com.jhr.algoNote.domain.Problem;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProblemResponse {

    private Long id;
    private String title;
    private String site;
    private String url;
    private String name;
    private String contentText;
    //OneToMany
    private List<ProblemTagDto> problemTags;
    private List<ReviewDto> reviews;

    public ProblemResponse(Problem problem) {
        this.id = problem.getId();
        this.title = problem.getTitle();

        this.site = problem.getSite().getName();
        this.url = problem.getUrl();
        this.name = problem.getMember().getName();
        this.contentText = problem.getContent().getText();
        this.problemTags = problem.getProblemTags().stream()
            .map(pt -> new ProblemTagDto(pt.getTag().getName())).collect(toList());
        this.reviews = problem.getReviews().stream().map(r -> new ReviewDto(r.getTitle()))
            .collect(toList());

    }

    @Getter
    @AllArgsConstructor
    public class ReviewDto {

        private String Title;
    }

    @Getter
    @AllArgsConstructor
    public class ProblemTagDto {

        private String tagName;
    }


}
