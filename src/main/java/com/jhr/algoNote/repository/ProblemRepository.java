package com.jhr.algoNote.repository;

import com.jhr.algoNote.domain.Problem;
import com.jhr.algoNote.domain.QMember;
import com.jhr.algoNote.domain.QProblem;
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
public class ProblemRepository {

    private final EntityManager em;
    private final JPAQueryFactory jpaQueryFactory;

    public Long save(Problem problem) {
        em.persist(problem);
        return problem.getId();
    }

    public Problem findById(Long id) {
        return em.find(Problem.class, id);
    }

    QProblem problem = QProblem.problem;
    QMember member = QMember.member;

    public List<Problem> findAll(ProblemSearch problemSearch) {

        BooleanBuilder builder = new BooleanBuilder();

        // == 조회 ==
        return jpaQueryFactory
            .select(problem)
            .from(problem)
            .join(problem.member, member)
            .where(
                memberIdEq(problemSearch.getMemberId()),
                siteEq(problemSearch.getSite()),
                titleLike(problemSearch.getTitle()))
            .limit(1000)
            .fetch();

    }

    // == 동적 쿼리 생성==
    private BooleanExpression memberIdEq(Long memberId) {

        return problem.member.id.eq(memberId);
    }

    private BooleanExpression titleLike(String title) {
        if (title == null) {
            return null;
        }
        return problem.title.contains(title);
    }

    private BooleanExpression siteEq(String site) {
        if (site == null) {
            return null;
        }
        return problem.site.eq(site);
    }
}
