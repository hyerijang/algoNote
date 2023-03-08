package com.jhr.algoNote.dto;

import com.jhr.algoNote.domain.Site;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProblemCard {
    private String title;
    private String tagText;
    private String siteName;
    private Long id;
    private String writer;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    @Builder
    public ProblemCard(String title, String tagText, String siteName, Long id, String writer, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.title = title;
        this.tagText = tagText;
        this.siteName = siteName;
        this.id = id;
        this.writer = writer;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }




}
