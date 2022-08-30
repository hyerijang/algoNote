package com.jhr.algoNote.api.controller;


import com.jhr.algoNote.api.dto.CreateMemberResponse;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Role;
import com.jhr.algoNote.service.MemberService;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
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

        return new CreateMemberResponse(memberService.join(member));
    }

    @Data
    static class CreateMemberRequest {

        private String name;
        @NotEmpty(message = "회원 이메일은 필수입니다")
        private String email;
        private String picture;
    }

    @PatchMapping("/{id}")
    public updateMemberResponse update(
        @PathVariable Long id,
        @RequestBody @Valid UpdateMemberRequest updateMemberRequest) {

        Member findMember = memberService.findOne(id);
        memberService.update(id, updateMemberRequest.getName(), updateMemberRequest.getPicture());
        return new updateMemberResponse(id, updateMemberRequest.getName(),
            updateMemberRequest.getPicture());

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
        return new Result(memberService.getMembers());
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }


}
