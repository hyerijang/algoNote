package com.jhr.algoNote.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class CreateProblemRequest {

    private String title;
    private String url;
    private String contentText;
    private String tagText;
    private String site;
    private Long memberId;

}


