package com.jhr.algoNote.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewDetails {
    private Long id;
    private Long problemId;
    private String writer;
    private String title;
    private String tagText;
    private  String ContentText;

    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @Builder

    public ReviewDetails(Long id, Long problemId, String writer, String title, String tagText, String contentText, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.problemId = problemId;
        this.writer = writer;
        this.title = title;
        this.tagText = tagText;
        ContentText = contentText;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }
}
