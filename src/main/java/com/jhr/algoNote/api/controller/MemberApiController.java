package com.jhr.algoNote.api.controller;


import com.jhr.algoNote.api.dto.CreateMemberRequest;
import com.jhr.algoNote.api.dto.CreateMemberResponse;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Role;
import com.jhr.algoNote.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/members")
public class MemberApiController {


    private final MemberService memberService;

    //URI
    private final String CREATE = "/new";

    @PostMapping(CREATE)
    public CreateMemberResponse create(
            @RequestBody @Valid CreateMemberRequest createMemberRequest) {

        Member member = Member.builder()
                .name(createMemberRequest.getName())
                .email(createMemberRequest.getEmail())
                .picture(createMemberRequest.getPicture())
                .role(Role.USER) // 기본권한 : ADMIN
                .build();

        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    @PatchMapping("/{id}")
    public updateMemberResponse update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateMemberRequest updateMemberRequest) {

        memberService.update(id, updateMemberRequest.getName(), updateMemberRequest.getPicture());
        Member member = memberService.findOne(id);
        return new updateMemberResponse(id, member.getName(), member.getPicture());

    }

    @Data
    static class UpdateMemberRequest {

        private String name;
        private String picture;

    }

    @Data
    @AllArgsConstructor
    static class updateMemberResponse {

        private Long id;
        private String name;
        private String picture;
    }

    @GetMapping
    public Result members() {
        List<Member> members = memberService.findMembers();
        List<MemberDto> collect = members.stream()
                .map(m -> new MemberDto(m.getId(), m.getName(), m.getPicture()))
                .collect(Collectors.toList());

        return new Result(collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private Long id;
        private String name;
        private String picture;
    }
}
