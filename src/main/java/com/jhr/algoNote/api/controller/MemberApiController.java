package com.jhr.algoNote.api.controller;


import com.jhr.algoNote.dto.CreateMemberRequest;
import com.jhr.algoNote.dto.CreateMemberResponse;
import com.jhr.algoNote.dto.Result;
import com.jhr.algoNote.dto.UpdateMemberRequest;
import com.jhr.algoNote.dto.updateMemberResponse;
import com.jhr.algoNote.service.MemberService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberApiController {


    private final MemberService memberService;

    @PostMapping("/new")
    public CreateMemberResponse create(
        @RequestBody @Valid CreateMemberRequest createMemberRequest) {
        return memberService.join(createMemberRequest);
    }

    @PatchMapping("/{id}")
    public updateMemberResponse update(
        @PathVariable Long id,
        @RequestBody @Valid UpdateMemberRequest updateMemberRequest) {
        return memberService.updateMember(id, updateMemberRequest);
    }



    @GetMapping
    public Result members() {
        return new Result(memberService.findMembers());
    }


}
