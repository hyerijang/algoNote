package com.jhr.algoNote.dto;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class CreateReviewRequest {

    private String title;
    @NotNull
    private String contentText;
    private String tagText;
    private Long problemId;


    @Builder
    public static CreateReviewRequest create(String title, String contentText, String tagText,
        Long problemId) {
        CreateReviewRequest createReviewRequest = new CreateReviewRequest();
        createReviewRequest.title = title;
        createReviewRequest.contentText = contentText;
        createReviewRequest.tagText = tagText;
        createReviewRequest.problemId = problemId;
        return createReviewRequest;
    }

}
