package com.jhr.algoNote.controller;


import com.jhr.algoNote.config.auth.LoginUser;
import com.jhr.algoNote.config.auth.dto.SessionUser;
import com.jhr.algoNote.controller.form.ProblemDetailsForm;
import com.jhr.algoNote.controller.form.ProblemForm;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.Review;
import com.jhr.algoNote.domain.Site;
import com.jhr.algoNote.dto.UpdateProblemRequest;
import com.jhr.algoNote.repository.query.ProblemSearch;
import com.jhr.algoNote.service.MemberService;
import com.jhr.algoNote.service.ProblemService;
import com.jhr.algoNote.service.TagService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/problems")
public class ProblemController {

    private final HttpSession httpSession;
    private final ProblemService problemService;
    private final MemberService memberService;
    private final TagService tagService;

    //URI
    private final String CREAT = "/new";
    private final String EDIT = "/{id}/edit";
    private final String SEARCH = "/search";
    private final String DETAILS = "/{id}";

    @GetMapping(CREAT)
    public String createForm(Model model) {
        model.addAttribute("problemForm", new ProblemForm());
        //사이트 정보
        model.addAttribute("sites", Site.values());
        return "problems/createProblemForm";
    }

    @PostMapping(CREAT)
    public String create(@Valid ProblemForm problemForm, BindingResult result,
        @LoginUser SessionUser user) {

        if (result.hasErrors()) {
            return "problems/createProblemForm";
        }

        Member member = memberService.findByEmail(user.getEmail());
        Long problemId = problemService.register(member.getId(), problemForm.getTitle(),
            problemForm.getContentText(), problemForm.getTagText(), problemForm.getSite(),
            problemForm.getUrl());
        log.debug("registered problem id ={}", problemId);

        return "redirect:/problems";
    }

    /**
     * 로그인한 유저가 등록한 문제 조회
     *
     * @param model
     * @return
     */
    @GetMapping
    public String list(Model model, @LoginUser SessionUser user) {

        ProblemSearch problemSearch = ProblemSearch.builder()
            .memberEmail(user.getEmail())
            .build();

        List<Problem> problems = problemService.search(0, 100, problemSearch);

        model.addAttribute("problems", problems);
        return "problems/problemList";
    }

    @GetMapping(EDIT)
    public String updateProblemForm(@PathVariable Long id, Model model) {
        Problem problem = problemService.findOne(id);
        ProblemForm form = new ProblemForm();
        //문제태그정보 text로 변환
        String tagText = problemService.getTagText(problem.getProblemTags());
        form.setId(problem.getId());
        form.setTitle(problem.getTitle());
        form.setUrl(problem.getUrl());
        form.setContentText(problem.getContent().getText());
        form.setTagText(tagText);
        form.setSite(problem.getSite().getName());
        model.addAttribute("problemForm", form);
        //사이트 정보
        model.addAttribute("sites", Site.values());
        return "problems/updateProblemForm";
    }

    @PostMapping(EDIT)
    public String edit(@Valid ProblemForm problemForm, BindingResult result,
        @LoginUser SessionUser user
    ) {

        if (result.hasErrors()) {
            return "problems/updateProblemForm";
        }
        Member member = memberService.findByEmail(user.getEmail());

        UpdateProblemRequest dto = UpdateProblemRequest.builder()
            .title(problemForm.getTitle())
            .contentText(problemForm.getContentText())
            .url(problemForm.getUrl())
            .tagText(problemForm.getTagText())
            .site(problemForm.getSite())
            .build();

        problemService.update(member.getId(),problemForm.getId(), dto);
        return "redirect:/problems";
    }

    @GetMapping(SEARCH)
    public String searchProblemForm(@ModelAttribute("problemSearch") ProblemSearch problemSearch,
        Model model, @LoginUser SessionUser user) {
        model.addAttribute("userEmail", user.getEmail());
        //자신의 문제만 검색 가능
        problemSearch.setMemberEmail(user.getEmail());

        //검색
        List<Problem> problems = problemService.search(0, 100, problemSearch);
        model.addAttribute("problems", problems);

        return "problems/problemSearch";
    }


    @GetMapping(DETAILS)
    public String ProblemDetailsForm(@PathVariable Long id, Model model) {
        Problem problem = problemService.findOne(id);
        ProblemDetailsForm form = new ProblemDetailsForm();
        //문제태그정보 text로 변환
        String tagText = problemService.getTagText(problem.getProblemTags());
        form.setId(problem.getId());
        form.setTitle(problem.getTitle());
        form.setUrl(problem.getUrl());
        form.setContentText(problem.getContent().getText());
        form.setTagText(tagText);
        form.setSite(problem.getSite().getName());

        //리뷰정보
        Map<Long, String> reviewInfo = new HashMap<>();
        for (Review r : problem.getReviews()) {
            reviewInfo.put(r.getId(), r.getTitle());
        }

        form.setReviewInfo(reviewInfo);
        model.addAttribute("form", form);
        //사이트 정보
        model.addAttribute("sites", Site.values());
        return "problems/problemDetails";
    }

}
