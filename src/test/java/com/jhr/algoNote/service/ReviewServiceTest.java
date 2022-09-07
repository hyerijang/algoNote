package com.jhr.algoNote.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.Review;
import com.jhr.algoNote.dto.CreateMemberRequest;
import com.jhr.algoNote.dto.CreateProblemRequest;
import com.jhr.algoNote.dto.CreateReviewRequest;
import com.jhr.algoNote.dto.MemberResponse;
import com.jhr.algoNote.dto.UpdateReviewRequest;
import com.jhr.algoNote.repository.ReviewRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
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


    /**
     * size 명의 Member 리스트를 DB에 등록한다.
     *
     * @param size
     * @return List of created Members
     */
    private List<MemberResponse> createMembers(int size) {
        //size 명의 member 생성
        for (int i = 1; i <= size; i++) {
            CreateMemberRequest request = new CreateMemberRequest("User" + i,
                "email" + i + "@gmail.com", "PICTURE");
            memberService.join(request);
        }
        return memberService.findMembers();
    }


    private List<Problem> createProblems(Long memberId, int size) {

        for (int i = 1; i <= size + 1; i++) {
            CreateProblemRequest request = CreateProblemRequest.builder().title("title" + i)
                .memberId(memberId)
                .contentText("content" + i)
                .build();
            problemService.register(request);
        }

        Member member = memberService.findOne(memberId);
        return member.getProblems();
    }

    //== 테스트 작성에 도움을 주는 메서드 끝==//

    @Test
    @DisplayName("리뷰를 등록한다")
    public void createReviewWithTags() {

        //given
        Long memberId = createMembers(3).get(0).getId();
        Problem problem = createProblems(memberId, 3).get(0);
        final String TAG_TEXT = "A,B,C";

        //when
        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
            .tagText(TAG_TEXT)
            .contentText("내용")
            .title("제목")
            .problemId(problem.getId())
            .build();

        Long savedId = reviewService.createReview(memberId, createReviewRequest); // 리뷰 저장

        //then
        Assertions.assertNotNull(savedId);
        Review savedReview = reviewRepository.findOne(savedId);

        String tagText = reviewService.getTagText(savedReview.getReviewTags());
        System.out.println("tagText = " + tagText);
        Assertions.assertEquals(3, savedReview.getReviewTags().size());
        Assertions.assertEquals("A", savedReview.getReviewTags().get(0).getTag().getName());
        Assertions.assertEquals(problem.getId(), savedReview.getProblem().getId());

    }

    @Test
    @DisplayName("존재하지 사용자가 리뷰 등록을 시도하면 예외가 발생해야한다")
    public void createReviewFromNotExistUser() {

        //given
        Long memberId = createMembers(3).get(0).getId();
        Problem problem = createProblems(memberId, 1).get(0);

        //when
        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
            .tagText("").contentText("내용").title("제목").problemId(problem.getId())
            .build();

        long NotExistUserId = Long.MAX_VALUE; // 존재하지 않는 사용자 ID
        //then
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.createReview(NotExistUserId, createReviewRequest);
        }, "존재하지 않는 유저 id로  리뷰를 등록할 수 없습니다.");

    }


    @Test
    @DisplayName("제목은 null 일 수 없다.")
    public void createReviewFromWithNoTitle() {
        //given
        Long memberId = createMembers(3).get(0).getId();
        Problem problem = createProblems(memberId, 1).get(0);

        //when
        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
            .tagText("")
            .contentText("내용")
            .problemId(problem.getId())
            .build();

        //then
        assertThrows(NullPointerException.class, () -> {
            reviewService.createReview(memberId, createReviewRequest);
        }, "제목은 null일 수 없습니다.");

    }

    @Test
    @DisplayName("내용의 text는 null 일 수 없다.")
    public void createReviewFromWithNoContentText() {
        //given
        Long memberId = createMembers(3).get(0).getId();
        Problem problem = createProblems(memberId, 1).get(0);

        //when
        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
            .tagText("")
            .title("")
            .problemId(problem.getId())
            .build();

        //then
        assertThrows(NullPointerException.class, () -> {
            reviewService.createReview(memberId, createReviewRequest);
        }, "내용의 text는 null일 수 없습니다.");

    }


    @Test
    @DisplayName("작성자와 수정자가 다름")
    public void updateRequestFromInvalidedUser() {

        //given
        List<MemberResponse> members = createMembers(3);
        Long writerId = members.get(0).getId();
        Problem problem = createProblems(writerId, 1).get(0);
        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
            .contentText("내용").title("제목").problemId(problem.getId())
            .build();
        Long reviewId = reviewService.createReview(writerId, createReviewRequest);

        //when
        UpdateReviewRequest updateReviewRequest = UpdateReviewRequest.builder().title("새로운제목")
            .build();
        Long modifierId = members.get(1).getId();// writer 와 다른 member
        //then
        assertThrows(IllegalArgumentException.class, () -> {
            reviewService.patch(modifierId, reviewId, updateReviewRequest);
        }, "작성자와 수정자가 다를 때 예외가 발생해야 합니다.");

    }


    @Test
    @DisplayName("특정 회원이 작성한 모든 리뷰 조회")
    void findReviews() {
        //given
        Long memberId = createMembers(3).get(0).getId();
        List<Problem> problemList = createProblems(memberId, 10);

        //리뷰등록
        for (int i = 0; i < 10; i++) {
            Long problemId = problemList.get(i).getId();
            CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
                .problemId(problemId)
                .title("TITLE" + i)
                .contentText("SAMPLE TEXT" + i)
                .tagText("")
                .build();

            Long reviewId = reviewService.createReview(memberId,
                createReviewRequest); //문제에 리뷰 추가
        }

        //when
        List<Review> reviewList = reviewService.findReviews(memberId);

        for (int i = 0; i < reviewList.size(); i++) {
            Review review = reviewList.get(i);
            System.out.println("review.getProblem().getId() = " + review.getProblem().getId());
        }
        //than
        assertEquals(10, reviewList.size());

    }


    @Test
    @DisplayName("리뷰 ID로 단건 조회")
    void findReview() {
        //given
        Long memberId = createMembers(3).get(0).getId();
        List<Problem> problemList = createProblems(memberId, 10);

        //리뷰등록

        Long problemId = problemList.get((int) (Math.random() % problemList.size()))
            .getId();
        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
            .problemId(problemId)
            .title("TITLE")
            .contentText("SAMPLE TEXT")
            .tagText("")
            .build();

        Long reviewId = reviewService.createReview(memberId, createReviewRequest); //문제에 리뷰 추가

        //when
        Review result = reviewService.findOne(reviewId);
        //than
        assertEquals("TITLE", result.getTitle());
        assertEquals(problemId, result.getProblem().getId());

    }

    @Test
    @DisplayName("리뷰에 태그 등록")
    void reviewTag() {
        //given
        Long memberId = createMembers(3).get(0).getId();
        List<Problem> problemList = createProblems(memberId, 10);

        //리뷰등록

        Long problemId = problemList.get((int) (Math.random() % problemList.size()))
            .getId(); // 유저의 문제 중 무작위 선정
        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
            .problemId(problemId)
            .title("TITLE")
            .contentText("SAMPLE TEXT")
            .tagText("A,B,C")
            .build();

        Long reviewId = reviewService.createReview(memberId, createReviewRequest); //문제에 리뷰 추가

        //when
        Review result = reviewService.findOne(reviewId);
        //than
        assertEquals(3, result.getReviewTags().size());
        assertEquals(result.getId(), result.getReviewTags().get(0).getReview().getId());

    }

    /*
    객체 상태에서도 정상 동작하기 위해서는 양방향 연관관계를 맺어줘야한다.
     */
    @Test
    @DisplayName("리뷰 - 문제 연관관계 메서드 적용 ")
    void problemReview() {
        //given
        Long memberId = createMembers(3).get(0).getId();

        List<Problem> problemList = createProblems(memberId, 10);

        //리뷰등록
        Problem problem = problemList.get((int) (Math.random() % problemList.size()));
        Long problemId = problem.getId();
        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
            .problemId(problemId)
            .title("TITLE")
            .contentText("SAMPLE TEXT")
            .tagText("")
            .build();
        Long reviewId = reviewService.createReview(memberId, createReviewRequest); //문제에 리뷰 추가

        //when
        Review result = reviewService.findOne(reviewId);

        //than
        assertEquals(problemId, result.getProblem().getId());
        assertEquals(1, problem.getReviews().size());

    }

    @Test
    @DisplayName("리뷰 수정")
    public void updateContent() {

        //given
        Long writerId = createMembers(3).get(0).getId();
        Problem problem = createProblems(writerId, 1).get(0);
        CreateReviewRequest createReviewRequest = CreateReviewRequest.builder()
            .contentText("내용").title("제목").tagText("태그").problemId(problem.getId())
            .build();
        Long reviewId = reviewService.createReview(writerId, createReviewRequest);

        final String NEW_TITLE = "새로운제목";
        final String NEW_CONTENT_TEXT = "새로운내용";
        final String NEW_TAG_TEXT = "새로운,태그들";
        UpdateReviewRequest updateReviewRequest = UpdateReviewRequest.builder()
            .title(NEW_TITLE)
            .contentText(NEW_CONTENT_TEXT)
            .tagText(NEW_TAG_TEXT)
            .build();

        //when
        reviewService.patch(writerId, reviewId, updateReviewRequest);
        Review result = reviewService.findOne(reviewId);

        //then
        assertAll(
            () -> assertEquals(reviewId, result.getId()),
            () -> assertEquals(NEW_CONTENT_TEXT, result.getContent().getText()),
            () -> assertEquals(NEW_TITLE, result.getTitle()),
            () -> assertEquals(NEW_TAG_TEXT, reviewService.getTagText(result.getReviewTags()))
            );


    }
}