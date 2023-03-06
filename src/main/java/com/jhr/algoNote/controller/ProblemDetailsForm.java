package com.jhr.algoNote.controller;

import java.util.Map;
import javax.validation.constraints.NotEmpty;

import com.jhr.algoNote.domain.Site;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProblemDetailsForm {

    @NotEmpty(message = "제목은 필수입니다")
    private String title;
    private String url;
    private String contentText;
    private String tagText;
    private String siteName;
    private Long id;
    /**
     * reviewId, title
     */
    private Map<Long,String> reviewInfo;


    public void setSiteName(String siteName) {
        if (siteName == null)
            throw new IllegalArgumentException("Site 명은 null일 수 없습니다.");

            //사이트 명이 등록되지 않은 경우
        else if (siteName == "") {
            return;
        }

        this.siteName = Site.valueOf(siteName).getName();
    }


}
