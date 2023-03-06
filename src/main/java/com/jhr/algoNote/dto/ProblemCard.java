package com.jhr.algoNote.dto;

import com.jhr.algoNote.domain.Site;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProblemCard {
    private String title;
    private String tagText;
    private String siteName;
    private Long id;

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
