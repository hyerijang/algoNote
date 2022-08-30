package com.jhr.algoNote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jhr.algoNote.domain.tag.Tag;
import com.jhr.algoNote.exception.RedundantTagNameException;
import com.jhr.algoNote.repository.TagRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TagServiceTest {

    @Autowired
    TagService tagService;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    ProblemService problemService;

    @Test
    void 태그_등록() throws Exception {
        // given
        Tag tag = Tag.builder().name("태그1").build();

        // when
        Long savedId = tagService.saveTag(tag);
        Tag findTag = tagRepository.findById(savedId);

        // then
        assertEquals(tag.getName(), findTag.getName());
    }

    @Test
    void 태그_이름_중복() throws Exception {
        // given
        String name = "태그";
        Tag tag1 = Tag.builder().name(name).build();
        Tag tag2 = Tag.builder().name(name).build();

        // when
        tagService.saveTag(tag1);

        assertThrows(RedundantTagNameException.class,
            () -> {
                tagService.saveTag(tag2); //태그 이름에 특수문자
            },
            "예외가 발생해야 한다.");

    }

    @Test
    void 태그_단건_조회() throws Exception {
        // given
        Tag tag = Tag.builder().name("태그1").build();
        Long savedId = tagService.saveTag(tag);

        // when
        Tag findTag = tagService.findOne(savedId);

        // then
        assertNotEquals(null, findTag.getName());
        assertEquals(tag.getName(), findTag.getName());
    }

    @Test
    void 태그_목록_조회() throws Exception {
        // given
        Tag tag1 = Tag.builder().name("태그1").build();
        Tag tag2 = Tag.builder().name("태그2").build();
        tagService.saveTag(tag1);
        tagService.saveTag(tag2);

        // when
        List<Tag> tagList = tagService.findTags();

        // then
        assertEquals(2, tagList.size());
    }


}