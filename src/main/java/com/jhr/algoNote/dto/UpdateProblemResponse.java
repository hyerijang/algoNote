package com.jhr.algoNote.dto;


import com.jhr.algoNote.domain.Problem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder(access = AccessLevel.PRIVATE)
public class UpdateProblemResponse {

    private Long id;
    private String title;
    private String url;
    private String contentText;
    private String tagText;
    private String site;

    public static UpdateProblemResponse of(Problem problem, String tagText) {
        return UpdateProblemResponse.builder()
            .id(problem.getId())
            .title(problem.getTitle())
            .url(problem.getUrl())
            .site(problem.getSite())
            .contentText(problem.getContent().getText())
            .tagText(tagText)
            .build();
    }
}
