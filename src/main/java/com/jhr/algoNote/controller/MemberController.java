package com.jhr.algoNote.controller;

import com.jhr.algoNote.controller.form.MemberForm;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Role;
import com.jhr.algoNote.dto.CreateMemberRequest;
import com.jhr.algoNote.dto.MemberResponse;
import com.jhr.algoNote.service.MemberService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    //URI
    private final String CREATE = "/new";
    @GetMapping(CREATE)
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping(CREATE)
    public String create(@Valid MemberForm memberForm, BindingResult result) {

        if (result.hasErrors()) {
            return "members/createMemberForm";
        }
        CreateMemberRequest request = new CreateMemberRequest(memberForm.getName(), memberForm.getEmail(),memberForm.getPicture());
        memberService.join(request);
        return "redirect:/";
    }

    @GetMapping
    public String list(Model model) {
        List<MemberResponse> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
