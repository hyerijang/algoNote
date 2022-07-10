package com.jhr.algoNote.service;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.content.ProblemContent;
import com.jhr.algoNote.domain.tag.ProblemTag;
import com.jhr.algoNote.domain.tag.Tag;
import com.jhr.algoNote.dto.ProblemUpdateRequest;
import com.jhr.algoNote.dto.ProblemCreateRequest;
import com.jhr.algoNote.repository.ProblemRepository;
import com.jhr.algoNote.repository.ProblemSearch;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProblemService {

    private final MemberService memberService;
    private final TagService tagService;
    private final ProblemRepository problemRepository;


    /**
     * register 메서드는 OCP를 위배하고 비효율적임 대신 registerWithDto 메서드 사용을 권장
     */
    @Transactional
    public Long register(@NonNull Long memberId, @NonNull String title, @NonNull String content) {
        return register(memberId, title, content, null, null, null);
    }

    /**
     * register 메서드는 OCP를 위배하고 비효율적임 대신 registerWithDto 메서드 사용을 권장
     */
    @Transactional
    public Long register(@NonNull Long memberId, @NonNull String title, @NonNull String content,
        String tagText) {
        return register(memberId, title, content, tagText, null, null);
    }

    /**
     * 문제 등록 with site and url
     */
    @Transactional
    public Long register(@NonNull Long memberId, @NonNull String title, @NonNull String content,
        String tagText, String site, String url) {
        //엔티티 조회
        Member member = memberService.findOne(memberId);

        //문제 내용 생성
        ProblemContent problemContent = ProblemContent.createProblemContent(content);

        //태그 생성
        List<ProblemTag> problemTagList = createProblemTagList(tagText);

        //문제 생성 후 제목, 내용, 태그 등록
        return problemRepository.save(
            Problem.builder()
                .member(member)
                .title(title)
                .content(problemContent)
                .problemTagList(problemTagList)
                .url(url)
                .site(site)
                .build()
        );
    }

    /**
     * tagNames 을 활용하여 problemTagList 생성
     */
    public List<ProblemTag> createProblemTagList(String tagText) {

        if (isStringEmpty(tagText)) { //태그가 입력되지 않은경우
            return new ArrayList<ProblemTag>();
        }

        String[] tagNames = TagService.sliceTextToTagNames(tagText);
        //문제태그 리스트 생성
        List<ProblemTag> problemTagList = new ArrayList<ProblemTag>();
        //태그 이름 조회 및 등록
        for (int i = 0; i < tagNames.length; i++) {
            Tag tag = tagService.findByName(tagNames[i]);
            if (tag == null) { //미 등록된 태그명이면 새로 등록
                tag = Tag.builder().name(tagNames[i]).build();
                tagService.saveTag(tag);
            }
            //문제태그에 태그 등록
            problemTagList.add(ProblemTag.createProblemTag(tag));
        }
        return problemTagList;
    }

    private boolean isStringEmpty(String str) {
        return str == null || str.isBlank(); //null이거나, 빈 문자열이거나, 공백만으로 이루어진 문자열인 경우 true를 리턴

    }


    /**
     * 검색
     */
    @Transactional
    public List<Problem> search(ProblemSearch problemSearch) {
        return problemRepository.findAll(problemSearch);
    }


    @Transactional
    public Long registerWithDto(@NonNull Long memberId, ProblemCreateRequest problemCreateRequest) {
        //엔티티 조회
        Member member = memberService.findOne(memberId);

        //문제 내용 생성
        ProblemContent problemContent = ProblemContent.createProblemContent(
            problemCreateRequest.getContentText());

        //태그 생성
        List<ProblemTag> problemTagList = createProblemTagList(problemCreateRequest.getTagText());

        //문제 생성 후 제목, 내용, 태그 등록
        return problemRepository.save(
            Problem.builder()
                .member(member)
                .title(problemCreateRequest.getTitle())
                .content(problemContent)
                .problemTagList(problemTagList)
                .url(problemCreateRequest.getUrl())
                .site(problemCreateRequest.getSite())
                .build()
        );
    }

    public Problem findOne(Long id) {
        return problemRepository.findById(id);
    }


    /**
     * 문제 수정, 수정시 요청자와 문제 작성자가 다르면 예외 발생
     *
     * @param memberId
     * @param problemUpdateRequest
     * @return
     */
    @Transactional
    public Long edit(@NonNull Long memberId, ProblemUpdateRequest problemUpdateRequest) {
        //엔티티 조회
        Member member = memberService.findOne(memberId);
        Problem problem = problemRepository.findById(problemUpdateRequest.getId());

        if (memberId != problem.getMember().getId()) {
            throw new IllegalArgumentException("작성자가 아닙니다.");
        }

        //문제 내용 수정
        problem.getContent().editText(problemUpdateRequest.getContentText());

        //태그 생성
        List<ProblemTag> problemTags = createProblemTagList(problemUpdateRequest.getTagText());

        //문제 정보 수정
        problem.update(problemUpdateRequest.getTitle(), problemUpdateRequest.getSite(),
            problemUpdateRequest.getUrl(), problemTags);

        return problem.getId();
    }

    /**
     * ProblemTagList를 String으로 변환
     *
     * @param problemTagList
     * @return
     */
    public String getTagText(List<ProblemTag> problemTagList) {
        if (problemTagList.size() == 0) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        for (ProblemTag problemTag : problemTagList) {
            sb.append(problemTag.getTag().getName());
            sb.append(",");
        }

        sb.setLength(sb.length() - 1); //마지막 ','제거
        return sb.toString();

    }
}
