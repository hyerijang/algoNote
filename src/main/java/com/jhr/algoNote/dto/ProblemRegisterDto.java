package com.jhr.algoNote.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProblemRegisterDto {
    private String title;
    private String url;
    private String contentText;
    private String tagText;
    private String siteName;

    @Builder
    public ProblemRegisterDto(String title, String url, String contentText, String tagText,
        String siteName) {
        this.title = title;
        this.url = url;
        this.contentText = contentText;
        this.tagText = tagText;
        this.siteName = siteName;
    }
}


