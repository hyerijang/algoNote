package com.jhr.algoNote.repository.query;

import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.QMember;
import com.jhr.algoNote.domain.QProblem;
import com.jhr.algoNote.domain.Site;
import com.jhr.algoNote.domain.content.QProblemContent;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProblemQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;

    // == QueryDSL== //
    QProblem problem = QProblem.problem;
    QMember member = QMember.member;
    QProblemContent problemContent = QProblemContent.problemContent;


    public List<Problem> search(int offset, int limit,
        ProblemSearch problemSearch) { //TODO : offset, limit 추가

        BooleanBuilder builder = new BooleanBuilder();
        // == 조회 ==
        return jpaQueryFactory
            .select(problem)
            .from(problem)
            .join(problem.member, member)
            .join(problem.content, problemContent)
            .where(
                siteEq(problemSearch.getSite()),
                titleLike(problemSearch.getTitle()),
                emailEq(problemSearch.getMemberEmail()),
                contentTextLike(problemSearch.getContentText())
            )
            .fetchJoin()
            .offset(offset)
            .limit(limit)
            .fetch();

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
        return problem.site.eq(Site.valueOf(site));
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

    public List<Problem> findAll(int offset, int limit) {

        return jpaQueryFactory
            .select(problem)
            .from(problem)
            .join(problem.member, member)
            .join(problem.content, problemContent)
            .limit(limit)
            .offset(offset)
            .fetchJoin()
            .fetch();
    }
}
