package com.jhr.algoNote.repository;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

//**
@Getter
public class ProblemSearch {

    private String title;
    private String siteName;
    
    @NonNull
    private Long memberId;
    //    private ProblemContent content;


    @Builder
    public ProblemSearch(String title, String siteName, Long memberId) {
        this.title = title;
        this.siteName = siteName;
        this.memberId = memberId;
    }
}
