package com.jhr.algoNote.controller;


import com.jhr.algoNote.config.auth.dto.SessionMember;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.dto.ProblemUpdateRequest;
import com.jhr.algoNote.dto.ProblemCreateRequest;
import com.jhr.algoNote.repository.ProblemSearch;
import com.jhr.algoNote.service.MemberService;
import com.jhr.algoNote.service.ProblemService;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProblemController {

    private final HttpSession httpSession;
    private final ProblemService problemService;
    private final MemberService memberService;

    @GetMapping("/problems/new")
    public String createForm(Model model) {
        model.addAttribute("form", new ProblemForm());
        return "problems/createProblemForm";
    }

    @PostMapping("/problems/new")
    public String creat(@Valid ProblemForm problemForm) {
        SessionMember user = (SessionMember) httpSession.getAttribute("user");

        Member member = null;

        if (user != null) {
            member = memberService.findByEmail(user.getEmail());
        }
        if (member != null) {
            ProblemCreateRequest dto = ProblemCreateRequest.builder()
                .title(problemForm.getTitle())
                .contentText(problemForm.getContentText())
                .url(problemForm.getUrl())
                .tagText(problemForm.getTagText())
                .siteName(problemForm.getSiteName())
                .build();
            Long problemId = problemService.registerWithDto(member.getId(), dto);
            log.info("registered problem id = {}", problemId);
        }
        return "redirect:/";
    }

    /**
     * 로그인한 유저가 등록한 문제 조회
     * @param model
     * @return
     */
    @GetMapping("/problems")
    public String list(Model model) {
        SessionMember user = (SessionMember) httpSession.getAttribute("user");
        Member member = null;
        if (user != null) {
            member = memberService.findByEmail(user.getEmail());
        }
        if (member != null) {

            ProblemSearch problemSearch = ProblemSearch.builder()
                .memberId(member.getId())
                .build();

            List<Problem> problems = problemService.search(problemSearch);

            log.info("problems size ={}", problems.size());

            model.addAttribute("problems", problems);
        }
        return "problems/problemList";
    }

    // TODO : 태그 조회 추가
    @GetMapping("/problems/{id}/edit")
    public String updateItemForm(@PathVariable Long id, Model model) {
        Problem problem = problemService.findOne(id);
        ProblemForm form = new ProblemForm();
        form.setId(problem.getId());
        form.setTitle(problem.getTitle());
        form.setUrl(problem.getUrl());
        form.setContentText(problem.getContent().getText());
//        form.setTagText(problem.getProblemTags());
        form.setSiteName(problem.getSiteName());
        model.addAttribute("form", form);
        return "problems/updateProblemForm";
    }

    @PostMapping("/problems/{id}/edit")
    public String edit(@ModelAttribute ProblemForm problemForm) {
        SessionMember user = (SessionMember) httpSession.getAttribute("user");
        Member member = null;
        if (user != null) {
            member = memberService.findByEmail(user.getEmail());
        }
        if (member != null) {
            ProblemUpdateRequest dto = ProblemUpdateRequest.builder()
                .title(problemForm.getTitle())
                .contentText(problemForm.getContentText())
                .url(problemForm.getUrl())
                .tagText(problemForm.getTagText())
                .siteName(problemForm.getSiteName())
                .id(problemForm.getId())
                .build();

            problemService.edit(member.getId(), dto);
        }
        return "redirect:/";
    }


}
