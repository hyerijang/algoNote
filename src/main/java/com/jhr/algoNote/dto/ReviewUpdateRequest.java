package com.jhr.algoNote.dto;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReviewUpdateRequest {
    private String title;
    @NotNull
    private String contentText ;
    private String tagText ;

}
