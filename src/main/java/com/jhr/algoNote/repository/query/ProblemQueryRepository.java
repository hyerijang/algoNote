package com.jhr.algoNote.repository.query;

import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.QMember;
import com.jhr.algoNote.domain.QProblem;
import com.jhr.algoNote.domain.content.QProblemContent;
import com.jhr.algoNote.domain.tag.QProblemTag;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProblemQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;

    // == QueryDSL== //
    QProblem problem = QProblem.problem;
    QMember member = QMember.member;
    QProblemTag problemTag = QProblemTag.problemTag;


    public List<Problem> search(ProblemSearch problemSearch) {
        BooleanBuilder builder = new BooleanBuilder();

        //검색 키워드 검색 ->  제목 or 내용 or 태그에 포함되면 검색 결과로 출력
        builder.and(titleLike(problemSearch.getKeyword())); //제목
        builder.or(contentTagName(problemSearch.getKeyword())); //태그
        builder.or(contentTextLike(problemSearch.getKeyword())); //내용

        // == 조회 ==
        List<Problem> result = jpaQueryFactory
                .select(problem)
                .from(problem)
                .join(problem.member, member).on(member.email.eq(problemSearch.getMemberEmail()))
                .where(builder)
                .limit(1000)
                .fetch();

        return result;
    }


    // == 동적 쿼리 생성==


    private BooleanExpression titleLike(String title) {
        if (title == null || title.isBlank()) {
            return null;
        }
        return problem.title.contains(title);
    }

    private BooleanExpression siteEq(String site) {
        if (site == null || site.isBlank()) {
            return null;
        }
        return problem.site.eq(site);
    }

    private BooleanExpression emailEq(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return problem.member.email.eq(email);
    }

    private BooleanExpression contentTextLike(String contentText) {
        if (contentText == null || contentText.isBlank()) {
            return null;
        }
        return problem.content.text.contains(contentText);
    }


    private BooleanExpression contentTagName(String tagName) {
        if (tagName == null || problem.problemTags == null) {
            return null;
        }
        return JPAExpressions.selectOne()
                .from(problemTag)
                .where(problemTag.problem.eq(problem),
                        problemTag.tag.name.eq(tagName))
                .exists();
    }


    public List<Problem> findAll(int offset, int limit) {
        QProblem problem = QProblem.problem;
        QMember member = QMember.member;
        QProblemContent problemContent = QProblemContent.problemContent;
        return jpaQueryFactory.select(problem)
                .from(problem)
                .join(problem.member, member)
                .join(problem.content, problemContent)
                .limit(limit)
                .offset(offset)
                .fetchJoin()
                .fetch();
    }
}
