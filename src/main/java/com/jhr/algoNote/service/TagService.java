package com.jhr.algoNote.service;

import com.jhr.algoNote.domain.tag.Tag;
import com.jhr.algoNote.exception.RedundantTagNameException;
import com.jhr.algoNote.repository.TagRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional
    public Long saveTag(Tag tag) {
        if (findByName(tag.getName()) != null) {
            throw new RedundantTagNameException("중복되는 태그 이름입니다.");
        }

        Long savedId = tagRepository.save(tag);
        log.info("Generate new tag : id = {},  name = {}", tag.getId(), tag.getName());
        return savedId;
    }

    public List<Tag> findTags() {
        return tagRepository.findAll();
    }

    public Tag findOne(Long itemId) {
        return tagRepository.findById(itemId);
    }

    public Tag findByName(String name) {
        return tagRepository.findByName(name);
    }


    /**
     * 1개의 문자열을 여러개의 태그이름으로 구분
     */
    public static String[] sliceTextToTagNames(String text) {
        text = stringReplace(text.trim()); //공백 제거
        String[] names = stringReplace(text)
            .split("\\s+"); // 공백문자 기준으로 자름
        return names;
    }

    /**
     * 한글, 숫자, 영문, 띄어쓰기,언더바, '-' 제외한 모든 특수문자 제거
     */
    private static String stringReplace(String str) {
        String match = "[^\uAC00-\uD7A30-9a-zA-Z\\-_]";
        str = str.replaceAll(match, " ");
        return str;
    }
}
