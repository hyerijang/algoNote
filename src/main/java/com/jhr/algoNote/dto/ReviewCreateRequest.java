package com.jhr.algoNote.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewCreateRequest {
    private String title;
    private String contentText ;
    private String tagText ;
    private Long problemId;


    @Builder
    public static ReviewCreateRequest create(String title, String contentText, String tagText,
        Long problemId) {
        ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest();
        reviewCreateRequest.title = title;
        reviewCreateRequest.contentText = contentText;
        reviewCreateRequest.tagText = tagText;
        reviewCreateRequest.problemId = problemId;
        return reviewCreateRequest;
    }

}
