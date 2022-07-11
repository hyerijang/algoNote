package com.jhr.algoNote.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.Review;
import com.jhr.algoNote.domain.Role;
import com.jhr.algoNote.dto.ProblemCreateRequest;
import com.jhr.algoNote.dto.ReviewCreateRequest;
import com.jhr.algoNote.repository.ReviewRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class ReviewServiceTest {

    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    ProblemService problemService;
    @Autowired
    TagService tagService;
    @Autowired
    ReviewService reviewService;

    // == 테스트 작성에 도움을 주는 메서드 시작 ==


    private List<Member> createMembers(int size) {
        ArrayList<Member> memberArrayList = new ArrayList<Member>();
        //size 명의 member 생성
        for (int i = 1; i <= size + 1; i++) {
            Member member = Member.builder()
                .name("User" + i)
                .email("email" + i + "@gmail.com")
                .role(Role.USER)
                .build();

            memberService.join(member);
            memberArrayList.add(member);
        }
        return memberArrayList;
    }


    private List<Problem> createProblems(Member member, int size) {

        for (int i = 1; i <= size + 1; i++) {
            ProblemCreateRequest pcr = ProblemCreateRequest.builder().title("title" + i)
                .contentText("content" + i)
                .build();
            problemService.register(member.getId(), pcr);
        }
        return member.getProblems();
    }

    //== 테스트 작성에 도움을 주는 메서드 끝==//

    @Test
    @DisplayName("리뷰를 등록한다")
    public void createReviewWithTags() {

        //given
        Member member = createMembers(3).get(0);
        Problem problem = createProblems(member, 3).get(0);
        final String TAG_TEXT = "A,B,C";

        //when
        ReviewCreateRequest reviewCreateRequest = ReviewCreateRequest.builder()
            .tagText(TAG_TEXT).contentText("내용").title("제목").problemId(problem.getId())
            .build();

        Long savedId = reviewService.createReview(member.getId(), reviewCreateRequest); // 리뷰 저장
        //then
        Assertions.assertNotNull(savedId);
        Review savedReview = reviewRepository.findOne(savedId);
        Assertions.assertEquals(3, savedReview.getReviewTags().size());
        Assertions.assertEquals("A", savedReview.getReviewTags().get(0).getTag().getName());
    }

    @Test
    @DisplayName("존재하지 사용자가 리뷰 등록을 시도하면 예외가 발생해야한다")
    public void createReviewFromNotExistUser() {

        //given
        final Member writer = createMembers(1).get(0);
        Problem problem = createProblems(writer, 1).get(0);

        //when
        ReviewCreateRequest reviewCreateRequest = ReviewCreateRequest.builder()
            .tagText("").contentText("내용").title("제목").problemId(problem.getId())
            .build();

        long NotExistUserId = Long.MAX_VALUE; // 존재하지 않는 사용자 ID
        //then
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.createReview(NotExistUserId, reviewCreateRequest);
        }, "존재하지 않는 유저 id로  리뷰를 등록할 수 없습니다.");

    }


    @Test
    @DisplayName("제목은 null 일 수 없다.")
    public void createReviewFromWithNoTitle() {
        //given
        final Member writer = createMembers(1).get(0);
        Problem problem = createProblems(writer, 1).get(0);

        //when
        ReviewCreateRequest reviewCreateRequest = ReviewCreateRequest.builder()
            .tagText("")
            .contentText("내용")
            .problemId(problem.getId())
            .build();

        //then
        assertThrows(NullPointerException.class, () -> {
            reviewService.createReview(writer.getId(), reviewCreateRequest);
        }, "제목은 null일 수 없습니다.");

    }

    @Test
    @DisplayName("내용의 text는 null 일 수 없다.")
    public void createReviewFromWithNoContentText() {
        //given
        final Member writer = createMembers(1).get(0);
        Problem problem = createProblems(writer, 1).get(0);

        //when
        ReviewCreateRequest reviewCreateRequest = ReviewCreateRequest.builder()
            .tagText("")
            .title("")
            .problemId(problem.getId())
            .build();

        //then
        assertThrows(NullPointerException.class, () -> {
            reviewService.createReview(writer.getId(), reviewCreateRequest);
        }, "내용의 text는 null일 수 없습니다.");

    }


    // FIXME : 리뷰 수정 만들고 다시 시도
    @Disabled
    @Test
    @DisplayName("작성자와 수정자가 다름")
    public void updateRequestFromInvalidedUser() {

        //given
        List<Member> members = createMembers(2);

        final Member writer = members.get(0);
        Problem problem = createProblems(writer, 1).get(0);
        final String TAG_TEXT = "A,B,C";

        //when
        ReviewCreateRequest reviewCreateRequest = ReviewCreateRequest.builder()
            .tagText(TAG_TEXT).contentText("내용").title("제목").problemId(problem.getId())
            .build();

        Member modifier = members.get(1); // writer 와 다른 member
        //then
        assertThrows(NullPointerException.class, () -> {
            reviewService.createReview(modifier.getId(), reviewCreateRequest);
        }, "작성자와 수정자가 다를 때 예외가 발생해야 합니다.");

    }


}