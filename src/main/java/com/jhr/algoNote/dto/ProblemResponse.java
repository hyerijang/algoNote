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

    private Long problemId;
    private String problemTitle;
    private String problemSite;
    private String problemUrl;
    private String name;
    private String problemContent;
    //OneToMany
    private List<ProblemTagDto> problemTags;
    private List<ReviewDto> reviews;

    public ProblemResponse(Problem problem) {
        this.problemId = problem.getId();
        this.problemTitle = problem.getTitle();
        this.problemSite = problem.getSite();
        this.problemUrl = problem.getUrl();
        this.name = problem.getMember().getName();
        this.problemContent = problem.getContent().getText();
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
