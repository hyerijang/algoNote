package com.jhr.algoNote.controller;

import java.util.Map;
import javax.validation.constraints.NotEmpty;
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
    private String site;
    private Long id;
    /**
     * reviewId, title
     */
    private Map<Long,String> reviewInfo;


}
