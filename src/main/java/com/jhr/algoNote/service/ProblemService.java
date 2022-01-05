package com.jhr.algoNote.service;

import com.jhr.algoNote.domain.Member;
import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.content.ProblemContent;
import com.jhr.algoNote.domain.tag.ProblemTag;
import com.jhr.algoNote.domain.tag.Tag;
import com.jhr.algoNote.repository.ProblemRepository;
import com.jhr.algoNote.repository.ProblemSearch;
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


    // ToDO : 현재의 register 메서드는 OCP 위배, 확장성 있는 코드로 개선할 것
    public Long register(@NonNull Long memberId, @NonNull String title, @NonNull String content) {
        return register(memberId, title, content, null, null, null);
    }

    public Long register(@NonNull Long memberId, @NonNull String title, @NonNull String content,
        String tagText) {
        return register(memberId, title, content, tagText, null, null);
    }

    /**
     * 문제 등록 with siteName and url
     */
    @Transactional
    public Long register(@NonNull Long memberId, @NonNull String title, @NonNull String content,
        String tagText, String siteName, String url) {
        //엔티티 조회
        Member member = memberService.findOne(memberId);

        //문제 내용 생성
        ProblemContent problemContent = ProblemContent.createProblemContent(content);

        //태그 생성
        ProblemTag[] problemTags = createProblemTagList(tagText);

        //문제 생성 후 제목, 내용, 태그 등록
        return problemRepository.save(
            Problem.builder()
                .member(member)
                .title(title)
                .content(problemContent)
                .tags(problemTags)
                .url(url)
                .siteName(siteName)
                .build()
        );
    }

    /**
     * tagNames 을 활용하여 ProblemTagList 생성
     */
    public ProblemTag[] createProblemTagList(String tagText) {

        if (isStringEmpty(tagText)) { //태그가 입력되지 않은경우
            return new ProblemTag[0];
        }

        String[] tagNames = TagService.sliceTextToTagNames(tagText);

        //문제태그 리스트 생성
        ProblemTag[] ProblemTagList = new ProblemTag[tagNames.length];
        //태그 이름 조회 및 등록
        for (int i = 0; i < tagNames.length; i++) {
            Tag tag = tagService.findByName(tagNames[i]);
            if (tag == null) { //미 등록된 태그명이면 새로 등록
                tag = Tag.builder().name(tagNames[i]).build();
                tagService.saveTag(tag);
            }
            //문제태그에 태그 등록
            ProblemTagList[i] = ProblemTag.createProblemTag(tag);
        }
        return ProblemTagList;
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
}
