package com.jhr.algoNote.controller;


import com.jhr.algoNote.config.auth.LoginUser;
import com.jhr.algoNote.config.auth.dto.SessionUser;
import com.jhr.algoNote.controller.form.ReviewForm;
import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Review;
import com.jhr.algoNote.dto.ReviewCreateRequest;
import com.jhr.algoNote.dto.ReviewUpdateRequest;
import com.jhr.algoNote.service.MemberService;
import com.jhr.algoNote.service.ReviewService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Slf4j
@Controller
@RequiredArgsConstructor
public class ReviewController {


    private final ReviewService reviewService;
    private final MemberService memberService;

    final String CREATE = "/problems/{problemId}/reviews/new";
    final String DETAILS = "/problems/{problemId}/reviews/{reviewId}";
    final String EDIT = "/problems/{problemId}/reviews/{reviewId}/edit";

    @GetMapping(CREATE)
    public String createForm(Model model, @PathVariable Long problemId) {
        ReviewForm reviewForm = new ReviewForm();
        reviewForm.setProblemId(problemId);
        model.addAttribute("reviewForm", reviewForm);
        //리뷰 생성 폼
        return "/reviews/createReviewForm";
    }


    @PostMapping(CREATE)
    public String create(@Valid ReviewForm reviewForm, BindingResult result,
        @PathVariable Long problemId,
        @LoginUser SessionUser user) {

        if (result.hasErrors()) {
            return "/reviews/createReviewForm";
        }

        Member member = memberService.findByEmail(user.getEmail());
        //리뷰 생성
        ReviewCreateRequest reviewCreateRequest = ReviewCreateRequest.builder()
            .title(reviewForm.getTitle())
            .tagText(reviewForm.getTagText())
            .problemId(reviewForm.getProblemId())
            .contentText(reviewForm.getContentText())
            .build();

        Long reviewId = reviewService.createReview(member.getId(), reviewCreateRequest);

        log.debug("review is created (problemId={}, reviewId={})", problemId, reviewId);
        return "redirect:/problems/{problemId}";
    }

    @GetMapping(DETAILS)
    public String detailsForm(Model model, @PathVariable Long problemId,
        @PathVariable Long reviewId) {
        Review review = reviewService.findOne(reviewId);

        ReviewForm reviewForm = new ReviewForm();
        reviewForm.setProblemId(review.getProblem().getId());
        reviewForm.setContentText(review.getContent().getText());
        String tagText = reviewService.getTagText(review.getReviewTags());
        reviewForm.setTagText(tagText);
        reviewForm.setTitle(review.getTitle());
        reviewForm.setReviewId(reviewId);

        model.addAttribute("form", reviewForm);
        return "/reviews/reviewDetailsForm";
    }


    @GetMapping(EDIT)
    public String editForm(Model model, @PathVariable Long reviewId) {

        Review find = reviewService.findOne(reviewId);
        ReviewForm reviewForm = new ReviewForm(find.getTitle(), find.getContent().getText(),
            reviewService.getTagText(find.getReviewTags()), find.getId(),reviewId);
        model.addAttribute("reviewForm", reviewForm);
        //리뷰 생성 폼
        return "/reviews/editReviewForm";
    }

    @PostMapping(EDIT)
    public String edit(@Valid ReviewForm reviewForm, BindingResult result,
        @PathVariable Long reviewId,
        @LoginUser SessionUser user) {

        if (result.hasErrors()) {
            return "/reviews/editReviewForm";
        }

        Member member = memberService.findByEmail(user.getEmail());
        //리뷰 수정
        ReviewUpdateRequest request = ReviewUpdateRequest
            .builder()
            .title(reviewForm.getTitle())
            .tagText(reviewForm.getTagText())
            .contentText(reviewForm.getContentText())
            .build();

        reviewService.edit(member.getId(), reviewId,request);

        log.debug("review is updated (reviewId={})", reviewId);
        return "redirect:/problems/{problemId}";
    }


}
