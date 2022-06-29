package com.jhr.algoNote.controller;


import com.jhr.algoNote.config.auth.dto.SessionMember;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.dto.ProblemRegisterDto;
import com.jhr.algoNote.repository.ProblemRepository;
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
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProblemController {

    private final HttpSession httpSession;
    private final ProblemService problemService;
    private final ProblemRepository problemRepository;
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
            ProblemRegisterDto dto = ProblemRegisterDto.builder()
                .title(problemForm.getTitle())
                .contentText(problemForm.getContentText())
                .url(problemForm.getUrl())
                .tagText(problemForm.getTagText())
                .siteName(problemForm.getSiteName())
                .build();
            Long problemId = problemService.registerWithDto(member.getId(),dto);
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

            log.info("problems size ={}",problems.size());

            model.addAttribute("problems", problems);
        }
        return "problems/problemList";
    }

}
