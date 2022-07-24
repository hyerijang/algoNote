package com.jhr.algoNote.controller;


import com.jhr.algoNote.config.auth.LoginUser;
import com.jhr.algoNote.config.auth.dto.SessionUser;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Review;
import com.jhr.algoNote.dto.ReviewCreateRequest;
import com.jhr.algoNote.service.MemberService;
import com.jhr.algoNote.service.ReviewService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@Controller
@RequiredArgsConstructor
public class ReviewController {


    private final ReviewService reviewService;
    private final MemberService memberService;

    final String CREATE = "/problems/{problemId}/reviews/new";
    final String DETAILS = "/problems/{problemId}/reviews/{reviewId}";

    @GetMapping(CREATE)
    public String createForm(Model model, @PathVariable Long problemId) {
        ReviewForm reviewForm = new ReviewForm();
        reviewForm.setProblemId(problemId);
        model.addAttribute("form", reviewForm);
        //리뷰 생성 폼
        return "/reviews/createReviewForm";
    }


    @PostMapping(CREATE)
    public String create(@Valid ReviewForm reviewForm, @PathVariable Long problemId,
        @LoginUser SessionUser user) {
        log.info("문제 {}의 리뷰생성", problemId);
        Member member = memberService.findByEmail(user.getEmail());
        //리뷰 생성
        ReviewCreateRequest reviewCreateRequest = ReviewCreateRequest.builder()
            .title(reviewForm.getTitle())
            .tagText(reviewForm.getTagText())
            .problemId(reviewForm.getProblemId())
            .contentText(reviewForm.getContentText())
            .build();

        reviewService.createReview(member.getId(), reviewCreateRequest);
        return "redirect:/problems/{problemId}";
    }

    @GetMapping(DETAILS)
    public String detailsForm(Model model, @PathVariable Long problemId,
        @PathVariable Long reviewId) {
        log.info("리뷰 단건 조회");
        Review review = reviewService.findOne(reviewId);

        ReviewForm reviewForm = new ReviewForm();
        reviewForm.setProblemId(review.getProblem().getId());
        reviewForm.setContentText(review.getTitle());
        String tagText = reviewService.getTagText(review.getReviewTags());
        reviewForm.setTagText(tagText);
        reviewForm.setTitle(review.getTitle());

        model.addAttribute("form", reviewForm);
        return "/reviews/reviewDetailsForm";
    }


}
