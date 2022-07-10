package com.jhr.algoNote.repository;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

//**
@Getter
public class ProblemSearch {

    private final String title;
    private final String site;
    
    @NonNull
    private final Long memberId;
    //    private ProblemContent content;


    @Builder
    public ProblemSearch(String title, String site, Long memberId) {
        this.title = title;
        this.site = site;
        this.memberId = memberId;
    }
}
