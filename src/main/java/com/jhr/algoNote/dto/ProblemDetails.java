package com.jhr.algoNote.dto;

import javax.validation.constraints.NotEmpty;

import com.jhr.algoNote.domain.Site;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProblemDetails {

    @NotEmpty(message = "제목은 필수입니다")
    private String title;
    private String url;
    private String contentText;
    private String tagText;
    private String siteName;
    private Long id;
    private String writer;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public void setSiteName(String siteName) {
        if (siteName == null)
            throw new IllegalArgumentException("Site 명은 null일 수 없습니다.");

            //사이트 명이 등록되지 않은 경우
        else if (siteName == "") {
            return;
        }

        this.siteName = Site.valueOf(siteName).getName();
    }

    @Builder
    public ProblemDetails(String title, String url, String contentText, String tagText, String siteName, Long id, String writer, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.title = title;
        this.url = url;
        this.contentText = contentText;
        this.tagText = tagText;
        this.siteName = siteName;
        this.id = id;
        this.writer = writer;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }
}
