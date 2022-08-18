package com.jhr.algoNote.repository.query;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

//**
@Getter
@Setter
public class ProblemSearch {
    //TODO : 태그 검색 기능 추가
    //TODO : 제목+내용 검색 추가

    private final String title;
    private final String site;
    private String memberEmail;
    private String contentText;


    @Builder
    public ProblemSearch(String title, String site, String memberEmail) {
        this.title = title;
        this.site = site;
        this.memberEmail = memberEmail;
        this.contentText = contentText;
    }
}
