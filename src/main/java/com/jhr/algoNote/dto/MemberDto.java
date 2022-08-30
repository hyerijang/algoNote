package com.jhr.algoNote.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberDto {
    private Long id;
    private String name;
    private String picture;
}
